package com.music.emotion.component;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.music.emotion.dto.AiAnalysisDTO;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

// 解析 AI 响应
@Component
public class MusicResponseParser {
    private final ObjectMapper objectMapper = new ObjectMapper();

    public AiAnalysisDTO parse(String response) throws Exception {
        String jsonStr = cleanJson(response);
        JsonNode root = objectMapper.readTree(jsonStr);

        AiAnalysisDTO dto = new AiAnalysisDTO();
        dto.setStyle(root.path("style").asText("未知"));
        dto.setEmotion(root.path("emotion").asText("未知"));

        validate(dto);
        return dto;
    }

    private String cleanJson(String raw) {
        if (raw == null) return "{}";
        String trimmed = raw.trim();
        // 移除可能的 markdown 代码块
        trimmed = trimmed.replaceAll("^```json\\s*", "");
        trimmed = trimmed.replaceAll("^```\\s*", "");
        trimmed = trimmed.replaceAll("\\s*```$", "");
        int start = trimmed.indexOf("{");
        int end = trimmed.lastIndexOf("}");
        if (start != -1 && end != -1 && end > start) {
            return trimmed.substring(start, end + 1);
        }
        return trimmed;
    }

    private void validate(AiAnalysisDTO result) {
        if (result == null) return;
        if (!StringUtils.hasText(result.getStyle())) result.setStyle("未知");
        if (!StringUtils.hasText(result.getEmotion())) result.setEmotion("未知");
    }

}