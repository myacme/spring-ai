package com.ljx.weather.service;

import com.ljx.weather.model.Location;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

/**
 * 地理位置提取服务
 */
@Service
public class LocationExtractorService {
    
    // 城市关键词映射
    private static final List<CityMapping> CITY_MAPPINGS = Arrays.asList(
        new CityMapping("北京", "北京", "北京市"),
        new CityMapping("上海", "上海", "上海市"),
        new CityMapping("广州", "广州", "广州市"),
        new CityMapping("深圳", "深圳", "深圳市"),
        new CityMapping("杭州", "杭州", "杭州市"),
        new CityMapping("南京", "南京", "南京市"),
        new CityMapping("成都", "成都", "成都市"),
        new CityMapping("武汉", "武汉", "武汉市"),
        new CityMapping("西安", "西安", "西安市"),
        new CityMapping("重庆", "重庆", "重庆市")
    );
    
    /**
     * 从文本中提取城市名称
     */
    public String extractCity(String text) {
        if (text == null || text.trim().isEmpty()) {
            return "北京"; // 默认城市
        }
        
        String lowerText = text.toLowerCase();
        
        // 直接匹配城市名称
        for (CityMapping mapping : CITY_MAPPINGS) {
            for (String keyword : mapping.keywords) {
                if (lowerText.contains(keyword.toLowerCase())) {
                    return mapping.cityName;
                }
            }
        }
        
        // 模糊匹配
        return fuzzyMatchCity(text);
    }
    
    /**
     * 模糊匹配城市
     */
    private String fuzzyMatchCity(String text) {
        // 简单的模糊匹配逻辑
        if (text.contains("首都") || text.contains("京城")) {
            return "北京";
        } else if (text.contains("魔都") || text.contains("沪")) {
            return "上海";
        } else if (text.contains("羊城") || text.contains("穗")) {
            return "广州";
        } else if (text.contains("鹏城")) {
            return "深圳";
        } else if (text.contains("天堂") || text.contains("杭城")) {
            return "杭州";
        } else if (text.contains("金陵") || text.contains("石头城")) {
            return "南京";
        }
        
        return "北京"; // 默认返回北京
    }
    
    /**
     * 验证城市是否支持
     */
    public boolean isSupportedCity(String city) {
        if (city == null || city.trim().isEmpty()) {
            return false;
        }
        
        for (CityMapping mapping : CITY_MAPPINGS) {
            if (mapping.cityName.equals(city)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * 获取城市完整信息
     */
    public Location getLocationInfo(String city) {
        if (!isSupportedCity(city)) {
            return null;
        }
        
        Location location = new Location();
        location.setCity(city);
        
        // 设置省份信息
        switch (city) {
            case "北京":
                location.setProvince("北京市");
                location.setCountry("中国");
                location.setLongitude(116.4074);
                location.setLatitude(39.9042);
                break;
            case "上海":
                location.setProvince("上海市");
                location.setCountry("中国");
                location.setLongitude(121.4737);
                location.setLatitude(31.2304);
                break;
            case "广州":
                location.setProvince("广东省");
                location.setCountry("中国");
                location.setLongitude(113.2644);
                location.setLatitude(23.1291);
                break;
            case "深圳":
                location.setProvince("广东省");
                location.setCountry("中国");
                location.setLongitude(114.0579);
                location.setLatitude(22.5431);
                break;
            case "杭州":
                location.setProvince("浙江省");
                location.setCountry("中国");
                location.setLongitude(120.1551);
                location.setLatitude(30.2741);
                break;
            case "南京":
                location.setProvince("江苏省");
                location.setCountry("中国");
                location.setLongitude(118.7969);
                location.setLatitude(32.0603);
                break;
            case "成都":
                location.setProvince("四川省");
                location.setCountry("中国");
                location.setLongitude(104.0665);
                location.setLatitude(30.5723);
                break;
            case "武汉":
                location.setProvince("湖北省");
                location.setCountry("中国");
                location.setLongitude(114.3054);
                location.setLatitude(30.5929);
                break;
            case "西安":
                location.setProvince("陕西省");
                location.setCountry("中国");
                location.setLongitude(108.9480);
                location.setLatitude(34.2632);
                break;
            case "重庆":
                location.setProvince("重庆市");
                location.setCountry("中国");
                location.setLongitude(106.5505);
                location.setLatitude(29.5638);
                break;
        }
        
        return location;
    }
    
    /**
     * 城市映射内部类
     */
    private static class CityMapping {
        private final String cityName;
        private final String[] keywords;
        
        public CityMapping(String cityName, String... keywords) {
            this.cityName = cityName;
            this.keywords = keywords;
        }
    }
}