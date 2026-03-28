package com.music.emotion.controller;

import com.music.emotion.common.Result;
import com.music.emotion.constant.MessageConstant;
import com.music.emotion.service.IMusicService;
import com.music.emotion.vo.AnalysisRecordVO;
import com.music.emotion.vo.SongVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequestMapping("/api/music")
public class MusicController {

    private final IMusicService musicService;

    public MusicController(IMusicService musicService) {
        this.musicService = musicService;
    }

    /**
     * 上传音乐 分析后 返回SongVO给前端
     * @param file
     * @return
     */
    @PostMapping("/upload")
    public Result<SongVO> uploadAndAnalyze(@RequestParam("file") MultipartFile file,
                                           @RequestParam(value = "songName", required = false) String songName,
                                           @RequestParam(value = "artist", required = false) String artist) {

        if (file == null || file.isEmpty()) {
            return Result.error("文件不能为空");
        }
        // 检查文件大小（限制 50MB）
        if (file.getSize() > 50 * 1024 * 1024) {
            return Result.error("文件大小不能超过 50MB");
        }

        SongVO songVO = musicService.uploadAnalyze(file, songName, artist);

        return Result.success(songVO);

    }

    @GetMapping("/latest/{songId}")
    public Result<AnalysisRecordVO> getLatestAnalysis(@PathVariable Long songId) {
        AnalysisRecordVO vo = musicService.getLatestAnalysisBySongId(songId);
        if (vo == null) {
            return Result.error(MessageConstant.NOT_EXIST);
        }
        return Result.success(vo);
    }

}
