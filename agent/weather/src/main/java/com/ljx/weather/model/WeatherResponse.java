package com.ljx.weather.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 天气查询响应实体
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class WeatherResponse {
    /**
     * 响应状态
     */
    private Status status;
    
    /**
     * 响应消息
     */
    private String message;
    
    /**
     * 天气信息列表
     */
    private List<WeatherInfo> weatherInfos;
    
    /**
     * 原始查询语句
     */
    private String originalQuery;
    
    /**
     * 解析后的查询参数
     */
    private QueryParameters queryParameters;
    
    public enum Status {
        SUCCESS,
        ERROR,
        PARTIAL
    }
    
    public static WeatherResponse success(String message, List<WeatherInfo> weatherInfos) {
        WeatherResponse response = new WeatherResponse();
        response.setStatus(Status.SUCCESS);
        response.setMessage(message);
        response.setWeatherInfos(weatherInfos);
        return response;
    }
    
    public static WeatherResponse error(String message) {
        WeatherResponse response = new WeatherResponse();
        response.setStatus(Status.ERROR);
        response.setMessage(message);
        return response;
    }
}