package com.music.emotion.service.impl;

import com.music.emotion.component.*;
import com.music.emotion.dto.AiAnalysisDTO;
import com.music.emotion.service.AiService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Service
@Slf4j
public class AiServiceImpl implements AiService {

    private final AudioFileManager fileManager;
    private final FeatureExtractor featureExtractor;
    private final MusicPromptBuilder promptBuilder;
    private final DeepSeekClient deepSeekClient;
    private final MusicResponseParser responseParser;

    public AiServiceImpl(AudioFileManager fileManager,
                         FeatureExtractor featureExtractor,
                         MusicPromptBuilder promptBuilder,
                         DeepSeekClient deepSeekClient,
                         MusicResponseParser responseParser) {
        this.fileManager = fileManager;
        this.featureExtractor = featureExtractor;
        this.promptBuilder = promptBuilder;
        this.deepSeekClient = deepSeekClient;
        this.responseParser = responseParser;
    }

    @Override
    public AiAnalysisDTO analyze(MultipartFile file) {
        Path tempFile = null;
        try {
            tempFile = Files.createTempFile("music_", ".tmp");
            file.transferTo(tempFile.toFile());
            // 提取特征
            String features = featureExtractor.extract(tempFile);

            log.info("特征提取完成：{}", features);

            //从特征字符串中提取数值（复用之前的解析方法）
            Map<String, Double> featureValues = extractFeatureValues(features);

            // 构建提示
            String prompt = promptBuilder.build(features);

            // 调用AI服务
            String response = deepSeekClient.analyze(prompt);
            log.info("AI 响应：{}", response);

            // 解析 AI 响应得到基础结果
            AiAnalysisDTO result = responseParser.parse(response);

            // 5. 将特征数值填充到 DTO 中
            result.setBpm(featureValues.get("bpm"));
            result.setEnergy(featureValues.get("energy"));
            result.setSpectralCentroid(featureValues.get("spectral_centroid"));
            result.setMelodicEntropy(featureValues.get("melodic_entropy"));

            return result;

        } catch (Exception e) {
            log.error("AI 分析失败", e);
            return createDefaultResult();
        } finally {
            fileManager.cleanup(tempFile);
        }
    }

    private AiAnalysisDTO createDefaultResult() {
        AiAnalysisDTO dto = new AiAnalysisDTO();
        dto.setStyle("未知");
        dto.setEmotion("未知");
        return dto;
    }


    // 提取特征数值的方法（可以放在工具类或本类中）
    private Map<String, Double> extractFeatureValues(String features) {
        Map<String, Double> map = new HashMap<>();
        Pattern p = Pattern.compile("BPM (\\d+\\.?\\d*)");
        Matcher m = p.matcher(features);
        if (m.find()) map.put("bpm", Double.parseDouble(m.group(1)));
        p = Pattern.compile("能量 (\\d+\\.?\\d*)");
        m = p.matcher(features);
        if (m.find()) map.put("energy", Double.parseDouble(m.group(1)));
        p = Pattern.compile("频谱质心 (\\d+\\.?\\d*)");
        m = p.matcher(features);
        if (m.find()) map.put("spectral_centroid", Double.parseDouble(m.group(1)));
        p = Pattern.compile("旋律多样性 (\\d+\\.?\\d*)");
        m = p.matcher(features);
        if (m.find()) map.put("melodic_entropy", Double.parseDouble(m.group(1)));
        return map;
    }
}