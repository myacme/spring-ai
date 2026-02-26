package com.ljx.weather.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * 天气信息实体
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class WeatherInfo {
    /**
     * 日期
     */
    private LocalDate date;
    
    /**
     * 城市
     */
    private String city;
    
    /**
     * 天气状况
     */
    private String condition;
    
    /**
     * 温度（摄氏度）
     */
    private Integer temperature;
    
    /**
     * 最高温度
     */
    private Integer highTemperature;
    
    /**
     * 最低温度
     */
    private Integer lowTemperature;
    
    /**
     * 湿度（百分比）
     */
    private Integer humidity;
    
    /**
     * 风力
     */
    private String wind;
    
    /**
     * 空气质量指数
     */
    private Integer aqi;
    
    /**
     * 空气质量描述
     */
    private String aqiDescription;
    
    /**
     * 降水概率（百分比）
     */
    private Integer precipitationProbability;
    
    /**
     * 紫外线指数
     */
    private Integer uvIndex;
    
    /**
     * 紫外线描述
     */
    private String uvDescription;
}