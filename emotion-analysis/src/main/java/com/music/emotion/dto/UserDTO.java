package com.music.emotion.dto;

import com.music.emotion.constant.MessageConstant;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class UserDTO {
    /**
     * 用户名
     * 用户名格式：4-16位字符（字母、数字、下划线、连字符）
     */
    @NotBlank(message = MessageConstant.USERNAME + MessageConstant.NOT_NULL)
    @Pattern(regexp = "^[a-zA-Z0-9_-]{4,16}$", message = MessageConstant.USERNAME + MessageConstant.FORMAT_ERROR)
    private String username;

    /**
     * 用户密码
     * 密码格式：8-18 位数字、字母、符号的任意两种组合
     */
    @NotBlank(message = MessageConstant.PASSWORD + MessageConstant.NOT_NULL)
    @Pattern(regexp = "^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z\\W]{8,18}$", message = MessageConstant.PASSWORD + MessageConstant.FORMAT_ERROR)
    private String password;

}
