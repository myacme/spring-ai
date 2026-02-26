package com.ljx.weather.service;

import com.ljx.weather.model.WeatherInfo;
import com.ljx.weather.model.WeatherQuery;
import com.ljx.weather.util.WeatherDataGenerator;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

/**
 * 天气数据查询服务
 */
@Service
public class WeatherService {
    
    /**
     * 查询当前天气
     */
    public WeatherInfo getCurrentWeather(String city) {
        if (!isSupportedCity(city)) {
            throw new IllegalArgumentException("不支持的城市: " + city);
        }
        return WeatherDataGenerator.generateWeather(city, LocalDate.now());
    }
    
    /**
     * 查询指定日期的天气
     */
    public WeatherInfo getWeatherByDate(String city, LocalDate date) {
        if (!isSupportedCity(city)) {
            throw new IllegalArgumentException("不支持的城市: " + city);
        }
        return WeatherDataGenerator.generateWeather(city, date);
    }
    
    /**
     * 查询天气预报
     */
    public List<WeatherInfo> getWeatherForecast(String city, int days) {
        if (!isSupportedCity(city)) {
            throw new IllegalArgumentException("不支持的城市: " + city);
        }
        if (days <= 0 || days > 7) {
            throw new IllegalArgumentException("预报天数必须在1-7天之间");
        }
        return WeatherDataGenerator.generateForecast(city, days);
    }
    
    /**
     * 处理天气查询请求
     */
    public List<WeatherInfo> queryWeather(WeatherQuery weatherQuery) {
        String city = weatherQuery.getCity();
        if (city == null || city.trim().isEmpty()) {
            city = "北京"; // 默认城市
        }
        
        switch (weatherQuery.getQueryType()) {
            case CURRENT:
                return Arrays.asList(getCurrentWeather(city));
            case FORECAST:
                int days = getForecastDays(weatherQuery.getDate());
                return getWeatherForecast(city, days);
            case HISTORY:
                LocalDate date = parseDate(weatherQuery.getDate());
                return Arrays.asList(getWeatherByDate(city, date));
            default:
                throw new IllegalArgumentException("不支持的查询类型");
        }
    }
    
    /**
     * 获取支持的城市列表
     */
    public String[] getSupportedCities() {
        return WeatherDataGenerator.getSupportedCities();
    }
    
    /**
     * 检查城市是否支持
     */
    private boolean isSupportedCity(String city) {
        String[] supportedCities = WeatherDataGenerator.getSupportedCities();
        for (String supportedCity : supportedCities) {
            if (supportedCity.equals(city)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * 根据日期描述获取预报天数
     */
    private int getForecastDays(String dateDescription) {
        if (dateDescription == null || dateDescription.trim().isEmpty()) {
            return 3; // 默认3天预报
        }
        
        dateDescription = dateDescription.toLowerCase();
        if (dateDescription.contains("一周") || dateDescription.contains("7天")) {
            return 7;
        } else if (dateDescription.contains("三天") || dateDescription.contains("3天")) {
            return 3;
        } else if (dateDescription.contains("明天")) {
            return 2; // 今天+明天
        } else if (dateDescription.contains("后天")) {
            return 3; // 今天+明天+后天
        } else {
            return 3; // 默认3天
        }
    }
    
    /**
     * 解析日期字符串
     */
    private LocalDate parseDate(String dateStr) {
        if (dateStr == null || dateStr.trim().isEmpty()) {
            return LocalDate.now();
        }
        
        // 简单的日期解析逻辑
        if (dateStr.contains("今天")) {
            return LocalDate.now();
        } else if (dateStr.contains("明天")) {
            return LocalDate.now().plusDays(1);
        } else if (dateStr.contains("后天")) {
            return LocalDate.now().plusDays(2);
        } else {
            // 尝试解析具体日期格式
            try {
                // 这里可以添加更复杂的日期解析逻辑
                return LocalDate.now(); // 简化处理
            } catch (Exception e) {
                return LocalDate.now();
            }
        }
    }
}