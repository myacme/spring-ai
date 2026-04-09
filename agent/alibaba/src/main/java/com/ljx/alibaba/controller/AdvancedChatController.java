package com.ljx.alibaba.controller;

import com.ljx.alibaba.entity.AdvancedChatRequest;
import com.ljx.alibaba.entity.RolePlayRequest;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 高级聊天控制器 - 演示系统提示词和上下文管理
 */
@RestController
@RequestMapping("/api/advanced")
@Slf4j
public class AdvancedChatController {

    @Resource(name = "qwenChatClient")
    private ChatClient chatClient;


    /**
     * 带系统提示词的聊天
     */
    @PostMapping("/chat-with-system")
    public String chatWithSystem(@RequestBody AdvancedChatRequest request) {
        return chatClient.prompt()
                .system(request.getSystemPrompt() != null ?
                        request.getSystemPrompt() :
                        "你是一个专业的AI助手，请用简洁专业的语言回答问题。")
                .user(request.getMessage())
                .call()
                .content();
    }

    /**
     * 角色扮演聊天
     */
    @PostMapping("/role-play")
    public String rolePlay(@RequestBody RolePlayRequest request) {
        String systemPrompt = switch (request.getRole().toLowerCase()) {
            case "teacher" -> "你是一位经验丰富的老师，擅长用简单易懂的方式解释复杂概念。";
            case "doctor" -> "你是一位专业的医生，提供健康建议但不替代专业医疗诊断。";
            case "programmer" -> "你是一位资深程序员，精通多种编程语言，能提供代码示例和最佳实践。";
            case "translator" -> "你是一位专业的翻译家，能够准确地在中文和英文之间翻译。";
            default -> "你是一个友好的助手。";
        };

        return chatClient.prompt()
                .system(systemPrompt)
                .user(request.getMessage())
                .call()
                .content();
    }
}
