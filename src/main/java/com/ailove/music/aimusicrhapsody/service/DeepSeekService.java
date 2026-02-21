package com.ailove.music.aimusicrhapsody.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DeepSeekService {

    @Value("${ai.api.key}")
    private String apiKey;

    private final WebClient webClient = WebClient.builder()
            .baseUrl("https://api.deepseek.com/v1")
            .defaultHeader("Authorization", "Bearer " + apiKey)
            .build();

    public Mono<String> getRecommendationFromAI(String prompt) {
        // 使用兼容Java 8的方式构建请求体
        Map<String, Object> requestMap = new HashMap<>();
        requestMap.put("model", "deepseek-reasoner");

        List<Map<String, String>> messages = new ArrayList<>();
        Map<String, String> systemMessage = new HashMap<>();
        systemMessage.put("role", "system");
        systemMessage.put("content", "你是一个专业的音乐推荐分析师。");
        messages.add(systemMessage);

        Map<String, String> userMessage = new HashMap<>();
        userMessage.put("role", "user");
        userMessage.put("content", prompt);
        messages.add(userMessage);

        requestMap.put("messages", messages);

        return webClient.post()
                .uri("/chat/completions")
                .bodyValue(requestMap)
                .retrieve()
                .bodyToMono(String.class);
    }
}