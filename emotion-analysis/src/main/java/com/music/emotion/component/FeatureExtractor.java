package com.music.emotion.component;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

// 调用Python脚本提取特征
@Component
public class FeatureExtractor {
    @Value("${music.python-executable}")
    private String pythonExecutable;
    @Value("${music.feature-extractor}")
    private String pythonScriptPath;

    public String extract(Path audioPath) throws Exception {
        ProcessBuilder pb = new ProcessBuilder(pythonExecutable, pythonScriptPath, audioPath.toString());

        // 设置环境变量，用于解决中文乱码
        pb.environment().put("PYTHONIOENCODING", "UTF-8");

        Process process = pb.start();

        // 使用UTF-8 读取标准输出
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream(),
                StandardCharsets.UTF_8));
        String features = reader.readLine();

        // 读取错误输出（用于调试）
        BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream(),
                StandardCharsets.UTF_8));
        StringBuilder errorMsg = new StringBuilder();
        String line;
        while ((line = errorReader.readLine()) != null) {
            errorMsg.append(line).append("\n");
        }

        int exitCode = process.waitFor();
        if (exitCode != 0) {
            throw new RuntimeException("特征提取失败，退出码：" + exitCode + "\n错误：" + errorMsg);
        }
        if (features == null || features.trim().isEmpty()) {
            throw new RuntimeException("特征提取结果为空，错误：" + errorMsg);
        }
        return features.trim();
    }
}