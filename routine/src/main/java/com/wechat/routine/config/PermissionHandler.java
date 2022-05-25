package com.wechat.routine.config;

import com.wechat.routine.enums.ExceptionEnum;
import com.wechat.routine.exceptions.BaseException;
import com.wechat.routine.utils.ApiResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.security.SignatureException;

/**
 * @author : HuangFu
 * @Description : 统一异常处理类
 * @date : 2022-05-24 11:17
 **/
@RestControllerAdvice
public class PermissionHandler {
    @ExceptionHandler(value = { SignatureException.class })
    @ResponseBody
    public ApiResponse authorizationException(SignatureException e){
        return ApiResponse.ofException(new BaseException(ExceptionEnum.FORBIDDEN));
    }
}
