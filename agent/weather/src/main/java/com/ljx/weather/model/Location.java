package com.ljx.weather.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 地理位置信息
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Location {
    /**
     * 城市名称
     */
    private String city;
    
    /**
     * 省份
     */
    private String province;
    
    /**
     * 国家
     */
    private String country = "中国";
    
    /**
     * 经度
     */
    private Double longitude;
    
    /**
     * 纬度
     */
    private Double latitude;
}