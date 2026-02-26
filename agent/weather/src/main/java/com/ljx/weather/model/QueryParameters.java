package com.ljx.weather.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 查询参数解析结果
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class QueryParameters {
    /**
     * 城市名称
     */
    private String city;
    
    /**
     * 查询日期描述
     */
    private String dateDescription;
    
    /**
     * 查询类型
     */
    private WeatherQuery.QueryType queryType;
    
    /**
     * 查询意图
     */
    private String intent;
    
    /**
     * 是否是未来日期查询
     */
    private Boolean isFutureDate;
    
    /**
     * 天数范围（用于预报）
     */
    private Integer days;
}