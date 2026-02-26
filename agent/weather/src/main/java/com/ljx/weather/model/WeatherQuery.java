package com.ljx.weather.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 天气查询请求实体
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class WeatherQuery {
    /**
     * 查询语句
     */
    private String query;
    
    /**
     * 城市名称
     */
    private String city;
    
    /**
     * 查询日期
     */
    private String date;
    
    /**
     * 查询类型
     */
    private QueryType queryType = QueryType.CURRENT;
    
    public enum QueryType {
        CURRENT,    // 当前天气
        FORECAST,   // 天气预报
        HISTORY     // 历史天气
    }
}