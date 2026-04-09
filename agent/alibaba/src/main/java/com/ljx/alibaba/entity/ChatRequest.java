package com.ljx.alibaba.entity;

import lombok.Data;

/**
 * 聊天请求
 */
@Data
public class ChatRequest {
    private String message;
    private String conversationId;  // 会话ID，用于区分不同用户的对话
}
