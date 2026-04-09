package com.ljx.weather.controller;


import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WeatherAgentController {

    private final ChatClient chatClient;

    public WeatherAgentController(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    @GetMapping("/ask")
    public String ask(@RequestParam String question) {
        return chatClient.prompt()
                .user(question)
                .call()
                .content();
    }

    @GetMapping("/weather")
    public String weather(@RequestParam String city) {
        return chatClient.prompt()
                .user("今天" + city + "的天气怎么样？适合出门吗？")
                .call()
                .content();
    }
    
    @GetMapping("/test-tool")
    public String testTool() {
        // 直接测试工具调用
        return chatClient.prompt()
                .user("请查询北京的天气")
                .call()
                .content();
    }
}
