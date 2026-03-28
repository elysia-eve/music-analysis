package com.music.emotion.service;

import com.music.emotion.common.Result;
import com.music.emotion.vo.AnalysisRecordVO;
import com.music.emotion.vo.SongVO;
import org.springframework.web.multipart.MultipartFile;

public interface IMusicService {
    SongVO uploadAnalyze(MultipartFile file, String songName, String artist);

    AnalysisRecordVO getLatestAnalysisBySongId(Long songId);

}
