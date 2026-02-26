package com.ljx.weather.controller;

import com.ljx.weather.model.WeatherQuery;
import com.ljx.weather.model.WeatherResponse;
import com.ljx.weather.service.WeatherAgentService;
import com.ljx.weather.service.WeatherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 天气查询控制器
 */
@RestController
@RequestMapping("/weather")
public class WeatherController {
    
    @Autowired
    private WeatherAgentService weatherAgentService;
    
    @Autowired
    private WeatherService weatherService;
    
    /**
     * 自然语言天气查询
     */
    @PostMapping("/query")
    public ResponseEntity<WeatherResponse> queryWeather(@RequestBody Map<String, String> request) {
        String query = request.get("query");
        if (query == null || query.trim().isEmpty()) {
            WeatherResponse response = WeatherResponse.error("查询语句不能为空");
            return ResponseEntity.badRequest().body(response);
        }
        
        WeatherResponse response = weatherAgentService.processQuery(query);
        return ResponseEntity.ok(response);
    }
    
    /**
     * 结构化天气查询
     */
    @PostMapping("/structured")
    public ResponseEntity<WeatherResponse> structuredQuery(@RequestBody WeatherQuery query) {
        try {
            var weatherInfos = weatherService.queryWeather(query);
            WeatherResponse response = WeatherResponse.success("查询成功", weatherInfos);
            response.setQueryParameters(null);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            WeatherResponse response = WeatherResponse.error("查询失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    /**
     * 获取支持的城市列表
     */
    @GetMapping("/cities")
    public ResponseEntity<String[]> getSupportedCities() {
        String[] cities = weatherService.getSupportedCities();
        return ResponseEntity.ok(cities);
    }
    
    /**
     * 健康检查接口
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> healthCheck() {
        Map<String, Object> health = new HashMap<>();
        health.put("status", "UP");
        health.put("service", "Weather Agent");
        health.put("timestamp", System.currentTimeMillis());
        return ResponseEntity.ok(health);
    }
    
    /**
     * API文档信息
     */
    @GetMapping("/api-info")
    public ResponseEntity<Map<String, Object>> getApiInfo() {
        Map<String, Object> info = new HashMap<>();
        info.put("service", "天气查询Agent");
        info.put("version", "1.0.0");
        info.put("description", "基于Spring AI的智能天气查询服务");
        
        Map<String, String> endpoints = new HashMap<>();
        endpoints.put("POST /weather/query", "自然语言天气查询");
        endpoints.put("POST /weather/structured", "结构化天气查询");
        endpoints.put("GET /weather/cities", "获取支持的城市列表");
        endpoints.put("GET /weather/health", "健康检查");
        endpoints.put("GET /weather/api-info", "API信息");
        
        info.put("endpoints", endpoints);
        
        return ResponseEntity.ok(info);
    }
}