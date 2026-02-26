package com.ljx.express.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Slf4j
@RequiredArgsConstructor
public class PhoneExtractorService {
    
    private final DeepSeekService deepSeekService;
    
    // 使用大模型智能提取手机号
    public String extractPhoneNumberWithAI(String input) {
        if (!StringUtils.hasText(input)) {
            return null;
        }
        
        try {
            // 构造AI提示词
            String prompt = String.format("""
                请从以下文本中提取手机号码：
                
                输入文本：%s
                
                要求：
                1. 只返回手机号码，不要其他内容
                2. 如果没有找到手机号码，返回"未找到"
                3. 手机号码应该是11位数字，以1开头
                """, input);
            
            String result = deepSeekService.chatCompletion(prompt);
            result = result.trim();
            
            // 验证提取结果是否为有效手机号
            if (!"未找到".equals(result) && isValidPhoneNumber(result)) {
                log.info("AI提取到手机号: {}", result);
                return result;
            }
            
            return null;
            
        } catch (Exception e) {
            log.error("AI手机号提取失败: {}", e.getMessage(), e);
            // 失败时回退到正则表达式
            return extractPhoneNumberFallback(input);
        }
    }
    
    // 正则表达式备选方案
    private String extractPhoneNumberFallback(String input) {
        Pattern PHONE_PATTERN = Pattern.compile("(?<!\\d)(1[3-9]\\d{9})(?!\\d)");
        Matcher matcher = PHONE_PATTERN.matcher(input);
        if (matcher.find()) {
            String phone = matcher.group(1);
            log.info("正则表达式提取到手机号: {}", phone);
            return phone;
        }
        return null;
    }
    
    private boolean isValidPhoneNumber(String phone) {
        return phone != null && phone.matches("1[3-9]\\d{9}");
    }
}