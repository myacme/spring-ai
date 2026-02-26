package com.ljx.express.service;

import com.ljx.express.model.ExpressResponse;
import com.ljx.express.model.QueryIntent;
import com.ljx.express.model.TrackingInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ExpressAgentService {
    
    private final PhoneExtractorService phoneExtractorService;
    private final Kuaidi100Service kuaidi100Service;
    
    // 智能快递查询主流程
    public ExpressResponse processExpressQuery(String userInput) {
        long startTime = System.currentTimeMillis();
        try {
            log.info("开始处理快递查询请求: {}", userInput);
            
            // 步骤1: 使用大模型提取手机号
            String phoneNumber = phoneExtractorService.extractPhoneNumberWithAI(userInput);
            
            if (phoneNumber == null) {
                return ExpressResponse.builder()
                    .success(false)
                    .message("抱歉，我没有在您的输入中找到有效的手机号码。请提供11位手机号码以便查询快递信息。")
                    .aiSuggestion("您可以这样询问：'帮我查一下13812345678的快递状态'")
                    .processingTime(System.currentTimeMillis() - startTime)
                    .build();
            }
            
            // 步骤2: 使用大模型增强的快递查询
            Map<String, Object> queryResult = kuaidi100Service.queryExpressInfoIntelligent(userInput, phoneNumber);
            
            // 步骤3: 构造智能响应
            return ExpressResponse.builder()
                .success(true)
                .message("快递信息查询成功")
                .extractedPhone(phoneNumber)
                .trackingInfos((List<TrackingInfo>) queryResult.get("trackingInfos"))
                .aiSummary((String) queryResult.get("aiSummary"))
                .queryIntent((QueryIntent) queryResult.get("queryIntent"))
                .processingTime(System.currentTimeMillis() - startTime)
                .build();
                
        } catch (Exception e) {
            log.error("处理快递查询请求失败: {}", e.getMessage(), e);
            return ExpressResponse.builder()
                .success(false)
                .message("处理请求时发生错误，请稍后重试")
                .errorMessage(e.getMessage())
                .processingTime(System.currentTimeMillis() - startTime)
                .build();
        }
    }
    
    // 批量查询服务
    public List<ExpressResponse> batchProcessQueries(List<String> userInputs) {
        return userInputs.parallelStream()
            .map(this::processExpressQuery)
            .collect(Collectors.toList());
    }
}