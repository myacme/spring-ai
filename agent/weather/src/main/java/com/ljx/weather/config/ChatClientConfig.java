package com.ljx.weather.config;


import com.ljx.weather.service.WeatherToolService;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ChatClientConfig {

    @Bean
    public ChatClient chatClient(ChatClient.Builder builder, WeatherToolService weatherToolService) {
        return builder
                .defaultSystem("你是一个天气助手，可以查询任何城市的实时天气。回答要简洁友好。")
                // 注册带有 @Tool 注解的 Bean
                .defaultTools(weatherToolService)
                .build();
    }
}
