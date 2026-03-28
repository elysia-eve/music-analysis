package com.music.emotion.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ResultCode {
    public static final int SUCCESS = 200;
    public static final int FAIL = 500;
    public static final int BAD_REQUEST = 400;
    public static final int NOT_FOUND = 404;
    public static final int UNAUTHORIZED = 401;

}
