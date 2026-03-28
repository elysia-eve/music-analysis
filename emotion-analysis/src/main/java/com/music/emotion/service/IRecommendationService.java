package com.music.emotion.service;

import com.music.emotion.common.Result;
import com.music.emotion.vo.SongVO;
import java.util.List;

public interface IRecommendationService {

    // 获取热门歌曲
    public List<SongVO> getHotSongs(int limit);

    // 基于相似歌曲推荐
    public List<SongVO> recommendBySimilarity(Long songId, int limit);
}
