package com.music.emotion.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.music.emotion.entity.AnalysisRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface AnalysisRecordMapper extends BaseMapper<AnalysisRecord> {
    default AnalysisRecord selectLatestBySongId(Long songId) {
        return selectOne(new LambdaQueryWrapper<AnalysisRecord>()
                .eq(AnalysisRecord::getSongId, songId)
                .orderByDesc(AnalysisRecord::getCreateTime)
                .last("limit 1"));
    }

    @Select("SELECT song_id, COUNT(*) as cnt FROM analysis_record GROUP BY song_id ORDER BY cnt DESC LIMIT #{limit}")
    List<Long> selectHotSongIds(int limit);

    // 统计用户各风格的分析次数（按次数降序）
    @Select("SELECT style, COUNT(*) as cnt FROM analysis_record WHERE user_id = #{userId} GROUP BY style ORDER BY cnt DESC")
    List<Map<String, Object>> countUserStyles(Long userId);

    // 统计用户各情绪的分析次数
    @Select("SELECT emotion, COUNT(*) as cnt FROM analysis_record WHERE user_id = #{userId} GROUP BY emotion ORDER BY cnt DESC")
    List<Map<String, Object>> countUserEmotions(Long userId);

    // 查询用户分析过的所有歌曲ID
    @Select("SELECT DISTINCT song_id FROM analysis_record WHERE user_id = #{userId}")
    List<Long> selectSongIdsByUserId(Long userId);


}
