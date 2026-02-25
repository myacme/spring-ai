package com.ljx.hander;


import org.springframework.ai.openai.api.common.OpenAiApiException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(OpenAiApiException.class)
    public ResponseEntity<String> handleOpenAIException(OpenAiApiException e) {
        return ResponseEntity.status(500)
                .body("DeepSeek API错误: " + e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGeneralException(Exception e) {
        return ResponseEntity.status(500)
                .body("服务器错误: " + e.getMessage());
    }
}