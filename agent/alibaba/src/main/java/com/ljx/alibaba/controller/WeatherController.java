package com.ljx.alibaba.controller;

import com.ljx.alibaba.entity.ChatRequest;
import com.ljx.alibaba.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 天气查询控制器 - 演示 Function Calling
 */
@RestController
@RequestMapping("/api/weather")
@RequiredArgsConstructor
public class WeatherController {
    
    private final ChatService chatService;
    
    /**
     * 智能天气查询（使用 Function Calling）
     * 通过聊天接口，ChatClient 会自动调用 weather tool
     */
    @PostMapping("/query")
    public String queryWeather(@RequestBody ChatRequest request) {
        return chatService.chat(request.getMessage());
    }
}
