package com.music.emotion.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.music.emotion.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {
}
