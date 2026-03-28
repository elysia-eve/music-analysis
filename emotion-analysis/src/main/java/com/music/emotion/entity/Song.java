package com.music.emotion.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("song")
public class Song {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 歌名 */
    private String songName;

    /** 歌手名 */
    private String artist;

    /** 文件 URL */
    private String fileUrl;

    /** 文件大小 (字节) */
    private Long fileSize;

    /** 时长 (秒) */
    private Integer duration;

    /** 创建时间 */
    private LocalDateTime createTime;

    /** 文件 Hash */
    @TableField(value = "file_hash")
    private String fileHash;
}
