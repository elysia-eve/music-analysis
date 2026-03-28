package com.music.emotion.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.Builder;

import java.time.LocalDateTime;

@Data
@Builder
public class AnalysisRecordVO {

    private String style;
    private String emotion;
    private String modelName;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;

    /**
     * 从实体转换到 VO
     */
    public static AnalysisRecordVO fromEntity(com.music.emotion.entity.AnalysisRecord record) {
        return AnalysisRecordVO.builder()
                .style(record.getStyle())
                .emotion(record.getEmotion())
                .modelName(record.getModelName())
                .createTime(record.getCreateTime())
                .build();
    }
}

