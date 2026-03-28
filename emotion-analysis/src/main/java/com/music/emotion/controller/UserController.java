package com.music.emotion.controller;

import com.music.emotion.common.Result;
import com.music.emotion.dto.UserDTO;
import com.music.emotion.service.IUserService;
import com.music.emotion.vo.SongVO;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/user")
public class UserController {

    private final IUserService userService;

    public UserController(IUserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public Result<String> register(@RequestBody @Valid UserDTO userDTO) {

        return userService.register(userDTO.getUsername(), userDTO.getPassword());
    }

    @PostMapping("/login")
    public Result<String> login(@RequestBody @Valid UserDTO userDTO) {
        try {
            String token = userService.login(userDTO.getUsername(), userDTO.getPassword());
            return Result.success(token);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/history")
    public Result<List<SongVO>> getHistory(){
        return userService.getHistory();
    }



}


