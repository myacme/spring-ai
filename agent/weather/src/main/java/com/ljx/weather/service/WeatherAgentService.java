package com.ljx.weather.service;

import com.ljx.weather.model.*;
import org.springframework.ai.chat.ChatClient;
import org.springframework.ai.chat.ChatResponse;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 天气Agent核心服务
 */
@Service
public class WeatherAgentService {
    
    @Autowired
    private ChatClient chatClient;
    
    @Autowired
    private WeatherService weatherService;
    
    @Autowired
    private LocationExtractorService locationExtractorService;
    
    @Autowired
    private DateParserService dateParserService;
    
    /**
     * 处理天气查询请求
     */
    public WeatherResponse processQuery(String query) {
        try {
            // 1. 使用AI分析查询意图
            WeatherIntent intent = analyzeIntent(query);
            
            if (intent.getIntentType() == WeatherIntent.IntentType.UNSUPPORTED_QUERY) {
                return WeatherResponse.error("抱歉，我无法理解您的天气查询请求");
            }
            
            // 2. 提取查询参数
            QueryParameters parameters = extractParameters(query, intent);
            
            // 3. 构建天气查询请求
            WeatherQuery weatherQuery = buildWeatherQuery(parameters);
            
            // 4. 查询天气数据
            List<WeatherInfo> weatherInfos = weatherService.queryWeather(weatherQuery);
            
            // 5. 生成自然语言响应
            String responseMessage = generateResponse(weatherInfos, parameters);
            
            // 6. 构建响应对象
            WeatherResponse response = WeatherResponse.success(responseMessage, weatherInfos);
            response.setOriginalQuery(query);
            response.setQueryParameters(parameters);
            
            return response;
            
        } catch (Exception e) {
            return WeatherResponse.error("处理查询时发生错误: " + e.getMessage());
        }
    }
    
    /**
     * 使用AI分析查询意图
     */
    private WeatherIntent analyzeIntent(String query) {
        String prompt = """
            请分析以下天气查询的意图，并以JSON格式返回结果：
            
            查询内容：%s
            
            请识别以下意图类型：
            - WEATHER_QUERY: 一般天气查询
            - TEMPERATURE_QUERY: 温度查询
            - RAIN_QUERY: 降水查询
            - FORECAST_QUERY: 天气预报查询
            - UNSUPPORTED_QUERY: 不支持的查询
            
            返回格式：
            {
                "intentType": "意图类型",
                "confidence": 置信度(0-1),
                "parameters": {
                    "city": "城市名称",
                    "dateDescription": "日期描述",
                    "queryType": "查询类型"
                }
            }
            """.formatted(query);
        
        try {
            Prompt promptObj = new Prompt(new UserMessage(prompt));
            ChatResponse response = chatClient.call(promptObj);
            String aiResponse = response.getResult().getOutput().getContent();
            
            // 简化处理，实际项目中应该解析JSON
            return parseIntentResponse(aiResponse, query);
            
        } catch (Exception e) {
            // AI调用失败时使用规则匹配
            return fallbackIntentAnalysis(query);
        }
    }
    
    /**
     * 解析AI意图响应
     */
    private WeatherIntent parseIntentResponse(String aiResponse, String query) {
        // 简化的解析逻辑，实际项目中应该使用JSON解析
        try {
            WeatherIntent intent = new WeatherIntent();
            intent.setQueryText(query);
            intent.setConfidence(0.8);
            
            // 根据AI响应内容判断意图类型
            String lowerResponse = aiResponse.toLowerCase();
            if (lowerResponse.contains("温度") || lowerResponse.contains("temp")) {
                intent.setIntentType(WeatherIntent.IntentType.TEMPERATURE_QUERY);
            } else if (lowerResponse.contains("雨") || lowerResponse.contains("降水")) {
                intent.setIntentType(WeatherIntent.IntentType.RAIN_QUERY);
            } else if (lowerResponse.contains("预报") || lowerResponse.contains("未来")) {
                intent.setIntentType(WeatherIntent.IntentType.FORECAST_QUERY);
            } else if (lowerResponse.contains("天气")) {
                intent.setIntentType(WeatherIntent.IntentType.WEATHER_QUERY);
            } else {
                intent.setIntentType(WeatherIntent.IntentType.UNSUPPORTED_QUERY);
            }
            
            return intent;
        } catch (Exception e) {
            return WeatherIntent.unsupported(query);
        }
    }
    
    /**
     * 意图分析降级处理
     */
    private WeatherIntent fallbackIntentAnalysis(String query) {
        String lowerQuery = query.toLowerCase();
        
        WeatherIntent intent = new WeatherIntent();
        intent.setQueryText(query);
        intent.setConfidence(0.7);
        
        if (lowerQuery.contains("温度") || lowerQuery.contains("几度")) {
            intent.setIntentType(WeatherIntent.IntentType.TEMPERATURE_QUERY);
        } else if (lowerQuery.contains("雨") || lowerQuery.contains("降水")) {
            intent.setIntentType(WeatherIntent.IntentType.RAIN_QUERY);
        } else if (lowerQuery.contains("预报") || lowerQuery.contains("未来") || 
                   lowerQuery.contains("几天")) {
            intent.setIntentType(WeatherIntent.IntentType.FORECAST_QUERY);
        } else if (lowerQuery.contains("天气")) {
            intent.setIntentType(WeatherIntent.IntentType.WEATHER_QUERY);
        } else {
            intent.setIntentType(WeatherIntent.IntentType.UNSUPPORTED_QUERY);
        }
        
        return intent;
    }
    
    /**
     * 提取查询参数
     */
    private QueryParameters extractParameters(String query, WeatherIntent intent) {
        QueryParameters parameters = new QueryParameters();
        
        // 提取城市
        String city = locationExtractorService.extractCity(query);
        parameters.setCity(city);
        
        // 提取日期信息
        DateParserService.ParsedDate parsedDate = dateParserService.parseDate(query);
        parameters.setDateDescription(parsedDate.getDescription());
        
        // 判断查询类型
        if (dateParserService.isForecastQuery(query)) {
            parameters.setQueryType(WeatherQuery.QueryType.FORECAST);
            parameters.setDays(dateParserService.getForecastDays(query));
        } else if (parsedDate.getDate().isBefore(java.time.LocalDate.now())) {
            parameters.setQueryType(WeatherQuery.QueryType.HISTORY);
        } else {
            parameters.setQueryType(WeatherQuery.QueryType.CURRENT);
        }
        
        // 设置意图
        parameters.setIntent(intent.getIntentType().name());
        
        // 判断是否是未来日期
        parameters.setIsFutureDate(dateParserService.isFutureDate(query));
        
        return parameters;
    }
    
    /**
     * 构建天气查询对象
     */
    private WeatherQuery buildWeatherQuery(QueryParameters parameters) {
        WeatherQuery query = new WeatherQuery();
        query.setCity(parameters.getCity());
        query.setDate(parameters.getDateDescription());
        query.setQueryType(parameters.getQueryType());
        query.setQuery(parameters.getIntent());
        return query;
    }
    
    /**
     * 生成自然语言响应
     */
    private String generateResponse(List<WeatherInfo> weatherInfos, QueryParameters parameters) {
        if (weatherInfos == null || weatherInfos.isEmpty()) {
            return "抱歉，未找到相关天气信息";
        }
        
        StringBuilder response = new StringBuilder();
        
        if (weatherInfos.size() == 1) {
            // 单日天气
            WeatherInfo weather = weatherInfos.get(0);
            response.append(String.format("%s%s的天气情况：", 
                parameters.getDateDescription(), weather.getCity()));
            response.append(String.format("天气%s，温度%d°C", 
                weather.getCondition(), weather.getTemperature()));
            
            if (parameters.getIntent().contains("TEMPERATURE")) {
                response.append(String.format("（最高%d°C，最低%d°C）", 
                    weather.getHighTemperature(), weather.getLowTemperature()));
            }
            
            if (weather.getHumidity() != null) {
                response.append(String.format("，湿度%d%%", weather.getHumidity()));
            }
            
            if (weather.getWind() != null) {
                response.append(String.format("，%s", weather.getWind()));
            }
            
        } else {
            // 多日预报
            response.append(String.format("%s未来%d天天气预报：\n", 
                parameters.getCity(), weatherInfos.size()));
            
            for (int i = 0; i < weatherInfos.size(); i++) {
                WeatherInfo weather = weatherInfos.get(i);
                response.append(String.format("%d.%s: %s，%d°C\n", 
                    i + 1, weather.getDate(), weather.getCondition(), weather.getTemperature()));
            }
        }
        
        return response.toString();
    }
}