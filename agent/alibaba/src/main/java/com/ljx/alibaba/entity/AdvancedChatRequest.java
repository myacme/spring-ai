package com.ljx.alibaba.entity;

import lombok.Data;

/**
 * 高级聊天请求
 */
@Data
public class AdvancedChatRequest {
    private String message;
    private String systemPrompt;
}
