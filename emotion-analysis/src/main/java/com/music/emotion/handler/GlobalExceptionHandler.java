package com.music.emotion.handler;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.music.emotion.common.Result;
import com.music.emotion.constant.MessageConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.support.MethodArgumentNotValidException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理参数校验异常（@RequestBody + @Valid）
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        String errorMessage = e.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));
        return Result.error(errorMessage);
    }

    /**
     * 处理参数绑定异常（@RequestParam + @Valid）
     */
    @ExceptionHandler(BindException.class)
    public Result handleBindException(BindException e) {
        String errorMessage = e.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));
        return Result.error(errorMessage);
    }

    /**
     * 处理文件上传大小超限
     */
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public Result handleMaxUploadSizeExceededException(MaxUploadSizeExceededException e) {
        return Result.error(MessageConstant.FILE_SIZE_LIMIT);
    }

    /**
     * 处理 JWT Token 过期异常
     */
    @ExceptionHandler(TokenExpiredException.class)
    public Result handleTokenExpiredException(TokenExpiredException e) {
        return Result.error(MessageConstant.TOKEN_EXPIRED);
    }

    /**
     * 处理 JWT 验证失败异常
     */
    @ExceptionHandler(JWTVerificationException.class)
    public Result handleJWTVerificationException(JWTVerificationException e) {
        return Result.fail(MessageConstant.INVALID_TOKEN);
    }

    /**
     * 处理数据库唯一约束冲突
     */
    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public Result handleSQLIntegrityConstraintViolationException(SQLIntegrityConstraintViolationException e) {
        String message = e.getMessage();
        
        return Result.error(MessageConstant.DATABASE_OPERATION_FAILED);
    }

    /**
     * 处理网络连接超时
     */
    @ExceptionHandler(SocketTimeoutException.class)
    public Result handleSocketTimeoutException(SocketTimeoutException e) {
        log.error("网络连接超时", e);
        return Result.fail(MessageConstant.FAILED);
    }

    /**
     * 处理网络连接失败
     */
    @ExceptionHandler(ConnectException.class)
    public Result handleConnectException(ConnectException e) {
        log.error("网络连接失败", e);
        return Result.fail(MessageConstant.NETWORK_CONNECTION_FAILED);
    }

    /**
     * 处理运行时异常
     */
    @ExceptionHandler(RuntimeException.class)
    public Result handleRuntimeException(RuntimeException e) {
        log.error("运行时异常", e);
        return Result.fail(e.getMessage());
    }

    /**
     * 处理通用异常
     */
    @ExceptionHandler(Exception.class)
    public Result handleException(Exception e) {
        log.error("系统异常", e);
        return Result.error(MessageConstant.ERROR);
    }

}
