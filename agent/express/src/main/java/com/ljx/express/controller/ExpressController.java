package com.ljx.express.controller;

import com.ljx.express.model.ExpressRequest;
import com.ljx.express.model.ExpressResponse;
import com.ljx.express.service.ExpressAgentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/express")
@Slf4j
@RequiredArgsConstructor
public class ExpressController {
    
    private final ExpressAgentService expressAgentService;
    
    @PostMapping("/query")
    public ResponseEntity<ExpressResponse> queryExpress(@RequestBody ExpressRequest request) {
        long startTime = System.currentTimeMillis();
        log.info("收到快递查询请求: {}", request.getMessage());
        
        ExpressResponse response = expressAgentService.processExpressQuery(request.getMessage());
        
        log.info("快递查询处理完成，耗时: {}ms", response.getProcessingTime());
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/batch-query")
    public ResponseEntity<List<ExpressResponse>> batchQueryExpress(
            @RequestBody List<ExpressRequest> requests) {
        List<String> messages = requests.stream()
            .map(ExpressRequest::getMessage)
            .collect(Collectors.toList());
            
        List<ExpressResponse> responses = expressAgentService.batchProcessQueries(messages);
        return ResponseEntity.ok(responses);
    }
    
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        Map<String, Object> healthInfo = Map.of(
            "status", "UP",
            "service", "Express Agent",
            "timestamp", System.currentTimeMillis()
        );
        return ResponseEntity.ok(healthInfo);
    }
    
    @GetMapping("/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("快递查询Agent服务运行正常");
    }
}