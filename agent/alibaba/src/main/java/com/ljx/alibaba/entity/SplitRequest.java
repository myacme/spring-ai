package com.ljx.alibaba.entity;

import lombok.Data;

/**
 * 分词请求
 */
@Data
public class SplitRequest {
    private String text;
    
    /**
     * 固定字数分词时的每块字数（默认500）
     */
    private Integer chunkSize;
}
