package com.music.emotion.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.music.emotion.entity.Song;
import lombok.Data;
import lombok.Builder;
import java.time.LocalDateTime;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SongVO {

    private Long id;


    private String songName;
    private String artist;

    private String fileUrl;

    private Integer duration;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;

    /** 最新分析结果 */
    private AnalysisRecordVO latestAnalysis;

    /**
     * 从实体转换到 VO
     */
    public static SongVO fromEntity(com.music.emotion.entity.Song song) {
        return SongVO.builder()
                .id(song.getId())
                .songName(song.getSongName())
                .artist(song.getArtist())
                .fileUrl(song.getFileUrl())
                .duration(song.getDuration())
                .createTime(song.getCreateTime())
                .build();
    }
}
