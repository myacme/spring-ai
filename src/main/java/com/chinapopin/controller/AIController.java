package com.chinapopin.controller;

import com.chinapopin.entity.ChatRequest;
import com.chinapopin.service.DeepSeekService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ai")
public class AIController {

    private final DeepSeekService deepSeekService;

    public AIController(DeepSeekService deepSeekService) {
        this.deepSeekService = deepSeekService;
    }

    @PostMapping("/chat")
    public String chat(@RequestBody ChatRequest request) {
        return deepSeekService.chat(request.getMessage());
    }

}