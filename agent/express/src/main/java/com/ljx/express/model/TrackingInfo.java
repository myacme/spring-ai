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
public class TrackingInfo {
    private String trackingNumber;
    private String carrier;
    private String status;
    private String updateTime;
    private List<TrackingEvent> events;
}