package com.music.emotion.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.music.emotion.entity.Song;
import com.music.emotion.entity.SongFeature;
import com.music.emotion.mapper.AnalysisRecordMapper;
import com.music.emotion.mapper.SongFeatureMapper;
import com.music.emotion.mapper.SongMapper;
import com.music.emotion.service.IRecommendationService;
import com.music.emotion.vo.SongVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
public class RecommendationService implements IRecommendationService {

    private SongMapper songMapper;
    private AnalysisRecordMapper analysisRecordMapper;
    private SongFeatureMapper songFeatureMapper;


    public RecommendationService(SongMapper songMapper, AnalysisRecordMapper analysisRecordMapper, SongFeatureMapper songFeatureMapper) {
        this.songMapper = songMapper;
        this.analysisRecordMapper = analysisRecordMapper;
        this.songFeatureMapper = songFeatureMapper;
    }

    @Override
    public List<SongVO> getHotSongs(int limit) {
        // 1. 获取热门歌曲 ID 列表（已按分析次数降序）
        List<Long> hotSongIds = analysisRecordMapper.selectHotSongIds(limit);
        if (hotSongIds.isEmpty()) {
            return Collections.emptyList();
        }

        // 2. 批量查询所有歌曲
        List<Song> songs = songMapper.selectBatchIds(hotSongIds);

        // 3. 将歌曲映射为 Map<id, Song>
        Map<Long, Song> songMap = songs.stream().collect(Collectors.toMap(Song::getId, Function.identity()));

        // 4. 按原 ID 列表顺序构建结果
        List<SongVO> orderedSongs = new ArrayList<>();
        for (Long id : hotSongIds) {
            Song song = songMap.get(id);
            if (song != null) {
                orderedSongs.add(SongVO.fromEntity(song));
            }
        }
        return orderedSongs;
    }

    @Override
    public List<SongVO> recommendBySimilarity(Long songId, int limit) {
        // 1. 获取当前歌曲特征
        SongFeature current = songFeatureMapper.selectById(songId);
        if (current == null) {
            return Collections.emptyList();
        }

        // 2. 构建归一化 + 加权特征向量（核心优化）
        double[] currentVec = buildWeightedFeatureVector(current);

        // 3. 查询所有其他歌曲（避免全表扫描，可加分页/缓存）
        List<SongFeature> others = songFeatureMapper.selectList(
                new LambdaQueryWrapper<SongFeature>()
                        .ne(SongFeature::getSongId, songId)
                        // 可选：只查询有完整特征的歌曲，避免脏数据
                        .isNotNull(SongFeature::getBpm)
                        .isNotNull(SongFeature::getEnergy)
        );
        if (others.isEmpty()) {
            return Collections.emptyList();
        }

        // 4. 计算相似度 + 排序
        List<SongWithScore> scoreList = new ArrayList<>(others.size());
        for (SongFeature other : others) {
            double[] otherVec = buildWeightedFeatureVector(other);
            double similarity = cosineSimilarity(currentVec, otherVec);
            scoreList.add(new SongWithScore(other.getSongId(), similarity));
        }

        // 相似度从高到低排序
        scoreList.sort((o1, o2) -> Double.compare(o2.score, o1.score));

        // 5. 取前N个，批量查歌曲（保持顺序）
        List<Long> recommendSongIds = scoreList.stream()
                .limit(limit)
                .map(SongWithScore::getSongId)
                .collect(Collectors.toList());

        if (recommendSongIds.isEmpty()) {
            return Collections.emptyList();
        }

        // 批量查询并保持顺序
        List<Song> songs = songMapper.selectBatchIds(recommendSongIds);
        Map<Long, Song> songMap = songs.stream()
                .collect(Collectors.toMap(Song::getId, s -> s, (old, newVal) -> old));

        // 组装返回结果
        List<SongVO> result = new ArrayList<>();
        for (Long id : recommendSongIds) {
            Song song = songMap.get(id);
            if (song != null) {
                result.add(SongVO.fromEntity(song));
            }
        }

        return result;
    }

    /**
     * 【优化核心】
     * 1. 正确归一化所有特征到 0~1
     * 2. 给关键特征加权（BPM最重要！）
     * 顺序：BPM、Energy、SpectralCentroid、MelodicEntropy
     */
    private double[] buildWeightedFeatureVector(SongFeature feature) {
        // 归一化（全部缩放到 0 ~ 1）
        double bpmNorm = normalize(feature.getBpm(), 40, 220);           // BPM 40~220
        double energyNorm = normalize(feature.getEnergy(), 0, 1);        // 能量 0~1
        double spectralNorm = normalize(feature.getSpectralCentroid(), 0, 10000); // 频谱重心 0~10000
        double melodyNorm = normalize(feature.getMelodicEntropy(), 0, 1); // 旋律熵 0~1

        // ====================== 核心优化：加权 ======================
        // BPM 权重最高（决定歌曲速度相似度）
        // 能量、频谱重心次之
        // 旋律熵权重稍低
        return new double[]{
                bpmNorm      * 2.5,   // BPM：最重要 ×2.5
                energyNorm   * 1.5,   // 能量 ×1.5
                spectralNorm * 1.2,   // 明亮度 ×1.2
                melodyNorm   * 0.8    // 旋律复杂度 ×0.8
        };
    }

    /**
     * 通用归一化工具：把任意数值缩放到 0~1
     */
    private double normalize(double value, double min, double max) {
        if (value <= min) return 0.0;
        if (value >= max) return 1.0;
        return (value - min) / (max - min);
    }

    /**
     * 余弦相似度（稳定版，防止除0）
     */
    private double cosineSimilarity(double[] vecA, double[] vecB) {
        double dotProduct = 0.0;
        double normA = 0.0;
        double normB = 0.0;

        for (int i = 0; i < vecA.length; i++) {
            dotProduct += vecA[i] * vecB[i];
            normA += vecA[i] * vecA[i];
            normB += vecB[i] * vecB[i];
        }

        double denominator = Math.sqrt(normA) * Math.sqrt(normB);
        // 防止分母为0（空特征）
        return denominator == 0 ? 0.0 : dotProduct / denominator;
    }

    /**
     * 歌曲 + 相似度得分
     */
    private static class SongWithScore {
        private final Long songId;
        private final double score;

        public SongWithScore(Long songId, double score) {
            this.songId = songId;
            this.score = score;
        }

        public Long getSongId() {
            return songId;
        }

        public double getScore() {
            return score;
        }
    }


}
