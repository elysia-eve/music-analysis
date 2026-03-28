package com.music.emotion.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AiAnalysisDTO {

    /** 音乐风格 */
    private String style;

    /** 情感标签 */
    private String emotion;

    // 四个特征字段
    private Double bpm;
    private Double energy;
    private Double spectralCentroid;
    private Double melodicEntropy;

}
