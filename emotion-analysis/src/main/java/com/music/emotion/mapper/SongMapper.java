package com.music.emotion.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.music.emotion.entity.Song;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SongMapper extends BaseMapper<Song> {

    // 根据文件哈希查询歌曲 去重需要
    default Song selectByHash(String hash) {
        return selectOne(new LambdaQueryWrapper<Song>().eq(Song::getFileHash, hash));
    }

}
