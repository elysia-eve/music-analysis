package com.music.emotion.component;

import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

// 音频文件管理器
// 下载、清理临时文件
@Component
public class AudioFileManager {
    public Path downloadToTemp(String url) throws IOException {
        Path tempFile = Files.createTempFile("music_", ".tmp");
        UrlResource resource = new UrlResource(url);
        Files.copy(resource.getInputStream(), tempFile, StandardCopyOption.REPLACE_EXISTING);
        return tempFile;
    }
    public void cleanup(Path path) {
        if (path != null) {
            try { Files.deleteIfExists(path); } catch (IOException ignored) {}
        }
    }
}