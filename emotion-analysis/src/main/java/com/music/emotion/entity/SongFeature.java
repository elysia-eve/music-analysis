package com.music.emotion.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("song_feature")
public class SongFeature {
    @TableId
    private Long songId;
    private Double bpm;
    private Double energy;
    private Double spectralCentroid;
    private Double melodicEntropy;
    private LocalDateTime updateTime;
}