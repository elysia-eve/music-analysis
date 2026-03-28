package com.music.emotion.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.music.emotion.dto.AiAnalysisDTO;
import com.music.emotion.entity.AnalysisRecord;
import com.music.emotion.entity.Song;
import com.music.emotion.entity.SongFeature;
import com.music.emotion.mapper.AnalysisRecordMapper;
import com.music.emotion.mapper.SongFeatureMapper;
import com.music.emotion.mapper.SongMapper;
import com.music.emotion.service.AiService;
import com.music.emotion.service.IMusicService;
import com.music.emotion.service.MinioService;
import com.music.emotion.util.UserContext;
import com.music.emotion.vo.AnalysisRecordVO;
import com.music.emotion.vo.SongVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;


@Service
public class MusicServiceImpl implements IMusicService {

    @Autowired
    private MinioService minioService;

    @Autowired
    private AiService aiService;

    @Autowired
    private SongMapper songMapper;

    @Autowired
    private AnalysisRecordMapper analysisRecordMapper;

    @Autowired
    private SongFeatureMapper songFeatureMapper;

    private final ObjectMapper objectMapper = new ObjectMapper();


    @Override
    @Transactional(rollbackFor = Exception.class)
    public SongVO uploadAnalyze(MultipartFile file, String songName, String artist){

        // 1. 计算文件哈希（假设 minioService 已有 calculateHash 方法）
        String fileHash;
        try {
            fileHash = minioService.calculateHash(file);
        } catch (Exception e) {
            throw new RuntimeException("计算文件哈希失败", e);
        }

        // 2. 查询是否已存在相同哈希的歌曲
        Song existingSong = songMapper.selectByHash(fileHash);
        if (existingSong != null) {
            // 已存在，直接返回已有的分析记录（最新的） 把分析记录复制一条并保存新的userId
            AnalysisRecord existingRecord = analysisRecordMapper.selectLatestBySongId(existingSong.getId());
            if (existingRecord != null) {
                Long currentUserId = UserContext.getCurrentUserId();
                // 仅当当前用户已登录，且与已有记录的用户不同时，才创建新记录
                if (currentUserId != null && !currentUserId.equals(existingRecord.getUserId())) {
                    AnalysisRecord newRecord = createAnalysisRecord(existingSong.getId(), existingRecord);
                    analysisRecordMapper.insert(newRecord);
                    return buildSongVO(existingSong, newRecord);
                } else {
                    // 未登录或用户已有记录，直接返回已有记录
                    return buildSongVO(existingSong, existingRecord);
                }
            }

        }


        //第一步 先按照文件名称处理歌曲和歌手名字
        String finalSongName = extractSongName(songName, file.getOriginalFilename());
        String finalArtist = StringUtils.hasText(artist)? artist : "未知歌手";

        //第二步 上传文件到minio
        String fileUrl = minioService.uploadFile(file, "vibe-music-data");
        Long fileSize = file.getSize();

        //第三步 把歌曲存入数据库
        Song song = new Song();
        song.setSongName(finalSongName);
        song.setArtist(finalArtist);
        song.setFileUrl(fileUrl);
        song.setFileSize(fileSize);
        song.setDuration(0);      //暂不处理时长
        song.setCreateTime(LocalDateTime.now());
        song.setFileHash(fileHash);

        songMapper.insert(song);

        //第四步 调用AI服务进行歌曲分析
        AiAnalysisDTO analyze = aiService.analyze(file);

        //第五步 保存歌曲分析结果
        AnalysisRecord record = createAnalysisRecord(song.getId(), analyze);
        analysisRecordMapper.insert(record);

        // 保存歌曲特征
        SongFeature feature = new SongFeature();
        feature.setSongId(song.getId());
        feature.setBpm(analyze.getBpm());
        feature.setEnergy(analyze.getEnergy());
        feature.setSpectralCentroid(analyze.getSpectralCentroid());
        feature.setMelodicEntropy(analyze.getMelodicEntropy());

        songFeatureMapper.insertOrUpdate(feature);

        //第六步 返回信息
        return buildSongVO(song, record);
    }

    @Override
    public AnalysisRecordVO getLatestAnalysisBySongId(Long songId) {
        AnalysisRecord record = analysisRecordMapper.selectLatestBySongId(songId);
        if (record == null) {
            throw new RuntimeException("歌曲不存在或未进行歌曲分析");
        }
        return AnalysisRecordVO.builder()
                .style(record.getStyle())
                .emotion(record.getEmotion())
                .build();
    }


    /**
     * 提取歌名（优先使用传入的，否则从文件名提取）
     */
    private String extractSongName(String songName, String filename) {
        // 如果前端传了歌名，直接使用
        if (StringUtils.hasText(songName)) {
            return songName.trim();
        }
        // 否则从文件名提取（去掉扩展名）
        if (StringUtils.hasText(filename)) {
            int lastDot = filename.lastIndexOf(".");
            if (lastDot > 0) {
                return filename.substring(0, lastDot).trim();
            }
            return filename.trim();
        }
        // 都没有则返回默认值
        return "未知歌曲";
    }

    // 创建歌曲分析记录
    private AnalysisRecord createAnalysisRecord(Long songId, AiAnalysisDTO aiResult) {
        AnalysisRecord record = new AnalysisRecord();
        record.setSongId(songId);

        //获取用户id
        record.setUserId(UserContext.getCurrentUserId());

        record.setStyle(aiResult.getStyle());
        record.setEmotion(aiResult.getEmotion());
        record.setModelName("DeepSeek");
        record.setCreateTime(LocalDateTime.now());
        return record;
    }

    /**
     * 从已有记录为当前用户创建分析记录
     */
    private AnalysisRecord createAnalysisRecord(Long songId, AnalysisRecord source) {
        AnalysisRecord record = new AnalysisRecord();
        record.setSongId(songId);
        record.setUserId(UserContext.getCurrentUserId());  // 设置当前用户
        record.setStyle(source.getStyle());
        record.setEmotion(source.getEmotion());
        record.setModelName(source.getModelName());
        record.setCreateTime(LocalDateTime.now());
        return record;
    }


    // 构建歌曲VO
    private SongVO buildSongVO(Song song, AnalysisRecord record) {
        SongVO songVO = SongVO.fromEntity(song);

        AnalysisRecordVO analysisVO = AnalysisRecordVO.builder()
                .style(record.getStyle())
                .emotion(record.getEmotion())
                .modelName(record.getModelName())
                .createTime(record.getCreateTime())
                .build();

        songVO.setLatestAnalysis(analysisVO);
        return songVO;
    }


}
