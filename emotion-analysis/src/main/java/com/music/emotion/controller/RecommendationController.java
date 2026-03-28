package com.music.emotion.controller;

import com.music.emotion.common.Result;
import com.music.emotion.service.IRecommendationService;
import com.music.emotion.vo.SongVO;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recommendations")
public class RecommendationController {

    private final IRecommendationService recommendationService;

    RecommendationController(IRecommendationService recommendationService){
        this.recommendationService = recommendationService;
    }

    @GetMapping("/hot")
    public Result<List<SongVO>> hotRecommend(@RequestParam(defaultValue = "10") int limit) {
        if (limit <= 0 || limit > 50) limit = 10;
        List<SongVO> songs = recommendationService.getHotSongs(limit);
        return Result.success(songs);
    }

    // 相似歌曲推荐
    @GetMapping("/similar/{songId}")
    public Result<List<SongVO>> similarRecommend(@PathVariable Long songId,
                                                 @RequestParam(defaultValue = "5") int limit) {
        if (limit <= 0 || limit > 20) limit = 5;
        List<SongVO> songs = recommendationService.recommendBySimilarity(songId, limit);
        return Result.success(songs);
    }
}



