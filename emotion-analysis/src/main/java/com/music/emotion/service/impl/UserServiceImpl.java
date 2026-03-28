package com.music.emotion.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.music.emotion.common.Result;
import com.music.emotion.constant.MessageConstant;
import com.music.emotion.entity.User;
import com.music.emotion.mapper.AnalysisRecordMapper;
import com.music.emotion.mapper.SongMapper;
import com.music.emotion.mapper.UserMapper;
import com.music.emotion.service.IUserService;
import com.music.emotion.util.JwtUtil;
import com.music.emotion.util.UserContext;
import com.music.emotion.vo.SongVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.music.emotion.common.Result.success;

@Slf4j
@Service
public class UserServiceImpl implements IUserService {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final AnalysisRecordMapper analysisRecordMapper;
    private final SongMapper songMapper;

    public UserServiceImpl(UserMapper userMapper, PasswordEncoder passwordEncoder, AnalysisRecordMapper analysisRecordMapper, SongMapper songMapper) {
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
        this.analysisRecordMapper = analysisRecordMapper;
        this.songMapper = songMapper;
    }
    @Override
    public Result<String> register(String username, String password) {

        //1. 判断用户名是否存在
        User userByUsername = userMapper.selectOne(new QueryWrapper<User>().eq("username", username));
        if (userByUsername != null) {
            return Result.error(MessageConstant.USERNAME + MessageConstant.ALREADY_EXISTS);
        }

        //2. 如果是全新的用户名 创建用户
        //进行加密
        String encodedPassword = passwordEncoder.encode(password);
        User user = new User();
        user.setUsername(username);
        user.setPassword(encodedPassword);
        //暂不设置头像
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());

        //插入数据库
        userMapper.insert(user);
        return success(MessageConstant.SUCCESS + MessageConstant.USER + ":" + username);
    }

    @Override
    public String login(String username, String password) {

        User user = userMapper.selectOne(new QueryWrapper<User>().eq("username", username));
        if (user == null) {
            throw new RuntimeException(MessageConstant.USERNAME + MessageConstant.NOT_EXIST);
        }
        if (BCrypt.checkpw(password, user.getPassword())) {
            //登录成功
            //生成 token
            Map<String, Object> claims = new HashMap<>();
            claims.put("userId", user.getUserId());
            claims.put("username", user.getUsername());
            return JwtUtil.generateToken(claims);
        }
        throw new RuntimeException(MessageConstant.PASSWORD + MessageConstant.ERROR);
    }

    @Override
    public Result<List<SongVO>> getHistory() {
        Long currentUserId = UserContext.getCurrentUserId();
        if (currentUserId == null) {
            return Result.error(MessageConstant.USER_NOT_LOGIN);
        }

        List<Long>songIds = analysisRecordMapper.selectSongIdsByUserId(currentUserId);
        if (songIds == null || songIds.isEmpty()) {
            return Result.success(null);
        }
        List<SongVO> songs = songMapper.selectBatchIds(songIds).stream().map(SongVO::fromEntity).collect(Collectors.toList());

        return Result.success(songs);
    }
}
