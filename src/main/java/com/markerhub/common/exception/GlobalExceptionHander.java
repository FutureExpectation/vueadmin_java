package com.markerhub.common.exception;

import com.markerhub.common.lang.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.nio.file.AccessDeniedException;
/*
*用来处理全局异常
* */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHander {
    @ExceptionHandler(value = RuntimeException.class)
    public Result handler(AccessDeniedException e){
        log.info("secuity权限不足：-------------------{}",e.getMessage());
        return Result.fail("权限不足");
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public Result handler(MethodArgumentNotValidException e){
        log.error("实体校验异常---------------{}",e.getMessage());
        BindingResult bindResult = e.getBindingResult();
        ObjectError objectError = bindResult.getAllErrors().stream().findFirst().get();
        return Result.fail(objectError.getDefaultMessage()+"实体校验异常");
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = IllegalArgumentException.class)
    public Result handler(IllegalArgumentException e){
        log.error("Assert异常：-------------------{}",e.getMessage());
        return Result.fail(e.getMessage()+"Assert异常");
    }

//    @ResponseStatus(HttpStatus.BAD_REQUEST)
//    @ExceptionHandler(value = RuntimeException.class)
//    public Result handler(RuntimeException e){
//        log.error("运行时异常：----------------{}",e.getMessage());
//        return Result.fail(e.getMessage());
//    }
}
