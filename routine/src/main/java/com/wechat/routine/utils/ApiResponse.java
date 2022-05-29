package com.wechat.routine.utils;


import com.wechat.routine.enums.ExceptionEnum;
import com.wechat.routine.exceptions.BaseException;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author : HuangFu
 * @Description : ResultMsg
 * @date : 2022-05-24 11:27
 **/
@Data
@ApiModel(value = "通用PI接口返回", description = "Common Api Response")
public class ApiResponse {
    /**
     * 通用返回状态
     */
    @ApiModelProperty(value = "通用返回状态", required = true)
    private Integer code;
    /**
     * 通用返回信息
     */
    @ApiModelProperty(value = "通用返回信息", required = true)
    private String message;
    /**
     * 通用返回数据
     */
    @ApiModelProperty(value = "通用返回数据", required = true)
    private Object data;

    private ApiResponse() {
    }

    private ApiResponse(Integer code, String message, Object data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    /**
     * 自定义Api返回
     *
     * @param code    状态码
     * @param message 返回内容
     * @param data    返回数据
     * @return ApiResponse
     */
    public static ApiResponse of(Integer code, String message, Object data) {
        return new ApiResponse(code, message, data);
    }

    /**
     * 构造一个成功且带数据的API返回
     *
     * @param data 返回数据
     * @return ApiResponse
     */
    public static ApiResponse ofSuccess(Object data) {
        return ofStatus(ExceptionEnum.OK, data);
    }

    /**
     * 构造一个成功且自定义消息的API返回
     *
     * @param message 返回内容
     * @return ApiResponse
     */
    public static ApiResponse ofMessage(String message) {
        return of(ExceptionEnum.OK.getCode(), message, null);
    }

    /**
     * 构造一个有状态的API返回
     *
     * @param status 状态 {@link ExceptionEnum}
     * @return ApiResponse
     */
    public static ApiResponse ofStatus(ExceptionEnum status) {
        return ofStatus(status, null);
    }

    /**
     * 构造一个有状态且带数据的API返回
     *
     * @param status 状态 {@link ExceptionEnum}
     * @param data   返回数据
     * @return ApiResponse
     */
    public static ApiResponse ofStatus(ExceptionEnum status, Object data) {
        return of(status.getCode(), status.getMsg(), data);
    }

    /**
     * 构造一个异常且带数据的API返回
     *
     * @param t    异常
     * @param data 返回数据
     * @param <T>  的子类
     * @return ApiResponse
     */
    public static <T extends BaseException> ApiResponse ofException(T t, Object data) {
        return of(t.getCode(), t.getMessage(), data);
    }

    /**
     * 构造一个异常且带数据的API返回
     *
     * @param t   异常
     * @param <T> {@link BaseException} 的子类
     * @return ApiResponse
     */
    public static <T extends BaseException> ApiResponse ofException(T t) {
        return ofException(t, null);
    }

}
