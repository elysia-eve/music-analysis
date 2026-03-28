package com.music.emotion.common;

import com.music.emotion.constant.ResultCode;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Result<T> {
    //状态码
    private Integer code;

    private String message;

    private T data;

    public static <T> Result<T> success(T data){
        return Result.<T>builder()
                .code(ResultCode.SUCCESS)
                .message("Success!")
                .data(data)
                .build();
    }

    public static <T> Result<T> error(String message){
        return Result.<T>builder()
                .code(ResultCode.FAIL)
                .message(message)
                .build();
    }

    public static <T> Result<T> fail(String message){
        return Result.<T>builder()
                .code(ResultCode.UNAUTHORIZED)
                .message(message)
                .build();
    }

}
