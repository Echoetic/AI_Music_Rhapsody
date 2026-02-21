package com.ailove.music.aimusicrhapsody.controller;

import com.ailove.music.aimusicrhapsody.model.RecommendedSong;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@SessionAttributes("recommendations") // 使用 Spring Session 管理
public class RecommendController {
    private static final Logger logger = LoggerFactory.getLogger(RecommendController.class);

    private final WebClient webClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public RecommendController(WebClient.Builder webClientBuilder,
                               @Value("${ai.api.base-url}") String baseUrl,
                               @Value("${ai.api.key}") String apiKey) {
        this.webClient = webClientBuilder
                .baseUrl(baseUrl)
                .defaultHeader("Authorization", "Bearer " + apiKey)
                .build();
    }

    @GetMapping("/recommend")
    public String showRecommendForm() {
        return "recommend";
    }

    @PostMapping("/recommend")
    public String handleRecommendation(
            @RequestParam(name = "songs") List<String> songs,
            Model model) {

        List<String> validSongs = new ArrayList<>();
        for (String song : songs) {
            if (song != null && !song.trim().isEmpty()) {
                validSongs.add(song);
            }
        }

        if (validSongs.isEmpty()) {
            model.addAttribute("error", "请输入至少一首歌曲名称！");
            return "recommend";
        }

        try {
            String songList = String.join("、", validSongs);
            String prompt = "你是一个专业的音乐推荐与分析专家。\n" +
                    "输入：我最近喜欢的歌曲有：" + songList + "。\n" +
                    "请基于以下四大维度推荐 3 首新歌，每首以 JSON 对象形式返回，格式如下：\n" +
                    "{\n" +
                    "  \"title\": \"歌曲名\",\n" +
                    "  \"reason\": \"推荐理由\",\n" +
                    "  \"score\": 推荐指数（1-5）,\n" +
                    "  \"genreMatch\": \"流派匹配说明\",\n" +
                    "  \"popularity\": \"知名度说明\",\n" +
                    "  \"lyricTheme\": \"歌词主题分析\",\n" +
                    "  \"emotion\": \"情绪风格匹配\"\n" +
                    "}\n" +
                    "最终只输出一个 JSON 数组，不带任何多余文字。";

            logger.info("调用AI推荐API，Prompt: {}", prompt);

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

            // 使用正确的API端点
            Mono<String> responseMono = webClient.post()
                    .uri("/chat/completions")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(requestMap)
                    .retrieve()
                    .bodyToMono(String.class);

            String responseJson = responseMono.block();
            logger.debug("API原始响应: {}", responseJson);

            // 正确解析嵌套的JSON响应
            JsonNode rootNode = objectMapper.readTree(responseJson);
            String content = rootNode
                    .path("choices")
                    .get(0)
                    .path("message")
                    .path("content")
                    .asText();

            logger.info("解析后的内容: {}", content);

            // 解析实际推荐内容
            List<RecommendedSong> recommendedSongs = objectMapper.readValue(
                    content,
                    new TypeReference<List<RecommendedSong>>() {}
            );

            // 将推荐结果添加到模型（也会保存到session）
            model.addAttribute("recommendations", recommendedSongs);
            model.addAttribute("inputSongs", validSongs);

            // 添加日志确认数据存储
            logger.info("推荐结果已存储到session: {} 首歌曲", recommendedSongs.size());
            return "recommend_result";

        } catch (Exception e) {
            logger.error("AI推荐服务异常", e);
            model.addAttribute("error", "AI推荐服务异常：" + e.getMessage());
            return "recommend";
        }
    }

    @GetMapping("/recommend/detail")
    public String showRecommendDetail(
            @SessionAttribute(name = "recommendations", required = false) List<RecommendedSong> recommendations,
            Model model) {

        // 添加详细日志
        if (recommendations == null) {
            logger.warn("Session中没有找到推荐数据");
        } else if (recommendations.isEmpty()) {
            logger.warn("Session中的推荐数据为空");
        } else {
            logger.info("从Session中获取到推荐数据: {} 首歌曲", recommendations.size());
        }

        if (recommendations == null || recommendations.isEmpty()) {
            // 如果没有推荐数据，重定向到推荐页面
            logger.warn("重定向到推荐表单页面");
            return "redirect:/recommend";
        }

        // 将推荐结果添加到模型
        model.addAttribute("recommendations", recommendations);
        return "recommend_detail";
    }
}