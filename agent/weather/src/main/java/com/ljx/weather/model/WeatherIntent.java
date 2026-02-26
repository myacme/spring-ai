package com.ljx.weather.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 天气查询意图识别
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class WeatherIntent {
    /**
     * 意图类型
     */
    private IntentType intentType;
    
    /**
     * 置信度
     */
    private Double confidence;
    
    /**
     * 原始查询文本
     */
    private String queryText;
    
    /**
     * 解析后的参数
     */
    private QueryParameters parameters;
    
    public enum IntentType {
        WEATHER_QUERY,      // 天气查询
        TEMPERATURE_QUERY,  // 温度查询
        RAIN_QUERY,         // 降水查询
        FORECAST_QUERY,     // 天气预报
        UNSUPPORTED_QUERY   // 不支持的查询
    }
    
    public static WeatherIntent of(IntentType type, Double confidence, String queryText) {
        return new WeatherIntent(type, confidence, queryText, null);
    }
    
    public static WeatherIntent unsupported(String queryText) {
        return new WeatherIntent(IntentType.UNSUPPORTED_QUERY, 1.0, queryText, null);
    }
}