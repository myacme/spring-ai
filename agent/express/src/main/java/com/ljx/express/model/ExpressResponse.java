package com.ljx.express.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExpressResponse {
    private boolean success;
    private String message;
    private String errorMessage;
    private List<TrackingInfo> trackingInfos;
    private String extractedPhone;
    private String aiSummary;  // AI生成的智能摘要
    private QueryIntent queryIntent;  // 用户查询意图
    private String aiSuggestion;  // AI建议
    private long processingTime;  // 处理耗时
}