package com.music.emotion.service;

import com.music.emotion.dto.AiAnalysisDTO;
import org.springframework.web.multipart.MultipartFile;

public interface AiService {

    AiAnalysisDTO analyze(MultipartFile file);

}
