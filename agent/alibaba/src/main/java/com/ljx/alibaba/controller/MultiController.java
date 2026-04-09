package com.ljx.alibaba.controller;


import jakarta.annotation.Resource;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

/**
 *
 *
 *
 *
 * @author ljx
 * @version 1.0.0
 * @create 2026/4/7 16:18
 */

@RestController
@RequestMapping("/api")
public class MultiController {

    //V2 通过ChatClient实现stream实现流式输出
    @Resource(name = "deepseekChatClient")
    private ChatClient deepseekChatClient;
    @Resource(name = "qwenChatClient")
    private ChatClient qwenChatClient;

    @GetMapping(value = "/stream/dp")
    public Flux<String> chatflux3(@RequestParam(name = "question",defaultValue = "你是谁") String question)
    {
        return deepseekChatClient.prompt(question).stream().content();
    }

    @GetMapping(value = "/stream/qw")
    public Flux<String> chatflux4(@RequestParam(name = "question",defaultValue = "你是谁") String question)
    {
        return qwenChatClient.prompt(question).stream().content();
    }



}
