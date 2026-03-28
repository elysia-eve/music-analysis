package com.music.emotion.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("analysis_record")
public class AnalysisRecord {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 关联歌曲 ID */
    private Long songId;

    /** 关联用户 ID */
    private Long userId;

    /** 音乐风格 */
    private String style;

    /** 情感标签 */
    private String emotion;

    /** AI 模型名称 */
    private String modelName;

    /** 分析时间 */
    private LocalDateTime createTime;
}
