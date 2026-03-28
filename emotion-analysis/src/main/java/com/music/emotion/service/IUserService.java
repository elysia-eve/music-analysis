package com.music.emotion.service;

import com.music.emotion.common.Result;
import com.music.emotion.vo.SongVO;

import java.util.List;

public interface IUserService {
    Result<String> register(String username, String password);

    String login(String username, String password);

    Result<List<SongVO>> getHistory();
}
