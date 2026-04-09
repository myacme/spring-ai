package com.ljx.alibaba.controller;

import com.ljx.alibaba.entity.ChatRequest;
import com.ljx.alibaba.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

/**
 * AI 聊天控制器
 */
@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class ChatController {
    
    private final ChatService chatService;
    
    /**
     * 普通聊天接口
     */
    @PostMapping("/chat")
    public String chat(@RequestBody ChatRequest request) {
        return chatService.chat(request.getMessage(), request.getConversationId());
    }

    /**
     * 流式聊天接口
     */
    @PostMapping(value = "/chat/stream", produces = "text/plain;charset=UTF-8")
    public Flux<String> chatStream(@RequestBody ChatRequest request) {
        return chatService.chatStream(request.getMessage(), request.getConversationId());
    }
    
    /**
     * 健康检查
     */
    @GetMapping("/health")
    public String health() {
        return "Spring AI Alibaba + DeepSeek is running!";
    }
}
