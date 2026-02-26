package com.ljx.express.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QueryIntent {
    private String mainPurpose;  // 主要目的
    private String urgency;      // 紧急程度
    private String specificRequirements;  // 特殊要求
}