package com.ljx.weather.service;



import com.ljx.weather.model.WeatherResponse;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Service;

@Service
public class WeatherToolService {

    @Tool(description = "获取指定城市的实时天气信息")
    public WeatherResponse getWeather(
            @ToolParam(description = "城市名称，如：北京、上海") String city) {

        // 实际开发中，请替换为真实的天气 API 调用（如和风天气、OpenWeatherMap）
        WeatherResponse mockWeatherData = getMockWeatherData(city);
        return mockWeatherData;
    }

    private WeatherResponse getMockWeatherData(String city) {
        return switch (city) {
            case "北京" -> new WeatherResponse(city, "25°C", "晴朗", "45%");
            case "上海" -> new WeatherResponse(city, "28°C", "多云", "65%");
            case "广州" -> new WeatherResponse(city, "32°C", "阵雨", "80%");
            case "深圳" -> new WeatherResponse(city, "30°C", "阴天", "75%");
            case "成都" -> new WeatherResponse(city, "22°C", "雾", "75%");
            default -> new WeatherResponse(city, "22°C", "未知", "50%");
        };
    }
}