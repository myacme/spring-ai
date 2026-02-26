package com.ljx.weather.util;

import com.ljx.weather.model.WeatherInfo;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * 天气数据生成器（模拟数据）
 */
public class WeatherDataGenerator {
    
    private static final Random random = new Random();
    
    // 城市列表
    private static final String[] CITIES = {
        "北京", "上海", "广州", "深圳", "杭州", "南京", "成都", "武汉", "西安", "重庆"
    };
    
    // 天气状况
    private static final String[] CONDITIONS = {
        "晴", "多云", "阴", "小雨", "中雨", "大雨", "暴雨", "雪", "雾", "霾"
    };
    
    // 风力描述
    private static final String[] WINDS = {
        "微风", "3-4级", "4-5级", "5-6级", "6-7级"
    };
    
    // 空气质量描述
    private static final String[] AQI_DESCRIPTIONS = {
        "优", "良", "轻度污染", "中度污染", "重度污染"
    };
    
    // 紫外线描述
    private static final String[] UV_DESCRIPTIONS = {
        "很弱", "弱", "中等", "强", "很强"
    };
    
    /**
     * 生成指定城市和日期的天气数据
     */
    public static WeatherInfo generateWeather(String city, LocalDate date) {
        WeatherInfo weather = new WeatherInfo();
        weather.setDate(date);
        weather.setCity(city);
        
        // 生成天气状况（根据季节调整概率）
        weather.setCondition(generateCondition(date));
        
        // 生成温度（根据城市和季节）
        int baseTemp = getBaseTemperature(city, date);
        int tempVariation = random.nextInt(10) - 5;
        int actualTemp = baseTemp + tempVariation;
        
        weather.setTemperature(actualTemp);
        weather.setHighTemperature(actualTemp + random.nextInt(5) + 2);
        weather.setLowTemperature(actualTemp - random.nextInt(5) - 2);
        
        // 生成其他参数
        weather.setHumidity(30 + random.nextInt(50)); // 30-80%
        weather.setWind(WINDS[random.nextInt(WINDS.length)]);
        weather.setAqi(50 + random.nextInt(150)); // 50-200
        weather.setAqiDescription(generateAqiDescription(weather.getAqi()));
        weather.setPrecipitationProbability(random.nextInt(100));
        weather.setUvIndex(1 + random.nextInt(10));
        weather.setUvDescription(UV_DESCRIPTIONS[Math.min(weather.getUvIndex() / 2, 4)]);
        
        return weather;
    }
    
    /**
     * 生成未来几天的天气预报
     */
    public static List<WeatherInfo> generateForecast(String city, int days) {
        List<WeatherInfo> forecast = new ArrayList<>();
        LocalDate today = LocalDate.now();
        
        for (int i = 0; i < days; i++) {
            LocalDate date = today.plusDays(i);
            forecast.add(generateWeather(city, date));
        }
        
        return forecast;
    }
    
    /**
     * 根据日期生成天气状况
     */
    private static String generateCondition(LocalDate date) {
        int month = date.getMonthValue();
        double rainProbability;
        
        // 根据月份调整降水概率
        if (month >= 6 && month <= 8) {
            rainProbability = 0.4; // 夏季多雨
        } else if (month >= 12 || month <= 2) {
            rainProbability = 0.2; // 冬季少雨
        } else {
            rainProbability = 0.3; // 春秋季
        }
        
        if (random.nextDouble() < rainProbability) {
            // 生成降水天气
            String[] rainConditions = {"小雨", "中雨", "大雨", "暴雨"};
            return rainConditions[random.nextInt(rainConditions.length)];
        } else {
            // 生成非降水天气
            String[] dryConditions = {"晴", "多云", "阴"};
            return dryConditions[random.nextInt(dryConditions.length)];
        }
    }
    
    /**
     * 根据城市和日期获取基础温度
     */
    private static int getBaseTemperature(String city, LocalDate date) {
        int month = date.getMonthValue();
        int baseTemp;
        
        // 根据月份确定基础温度
        if (month >= 6 && month <= 8) {
            baseTemp = 28; // 夏季
        } else if (month >= 12 || month <= 2) {
            baseTemp = 5;  // 冬季
        } else {
            baseTemp = 18; // 春秋季
        }
        
        // 根据城市调整温度
        switch (city) {
            case "北京":
                baseTemp -= 2;
                break;
            case "广州":
            case "深圳":
                baseTemp += 5;
                break;
            case "哈尔滨":
                baseTemp -= 10;
                break;
            case "海口":
                baseTemp += 8;
                break;
        }
        
        return baseTemp;
    }
    
    /**
     * 根据AQI值生成空气质量描述
     */
    private static String generateAqiDescription(int aqi) {
        if (aqi <= 50) {
            return "优";
        } else if (aqi <= 100) {
            return "良";
        } else if (aqi <= 150) {
            return "轻度污染";
        } else if (aqi <= 200) {
            return "中度污染";
        } else {
            return "重度污染";
        }
    }
    
    /**
     * 获取支持的城市列表
     */
    public static String[] getSupportedCities() {
        return CITIES;
    }
}