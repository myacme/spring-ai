package com.ljx.alibaba.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;

/**
 * 工具服务 - 演示 Function Calling 功能
 */
@Component
@Slf4j
public class WeatherToolService {

    /**
     * 获取天气信息（模拟数据）
     */
    @Tool(description = "获取指定城市的天气信息")
    public String getWeather(String city) {
        log.info("查询城市天气: {}", city);

        // 模拟天气数据
        return switch (city) {
            case "北京" -> "北京今天天气晴朗，温度25°C，湿度45%，风力3级";
            case "上海" -> "上海今天多云，温度28°C，湿度65%，风力2级";
            case "广州" -> "广州今天有阵雨，温度32°C，湿度80%，风力4级";
            case "深圳" -> "深圳今天阴天，温度30°C，湿度75%，风力3级";
            case "成都" -> "成都今天阴天，温度28°C，湿度75%，风力1级";
            default -> city + "的天气数据暂时无法获取";
        };
    }
}
