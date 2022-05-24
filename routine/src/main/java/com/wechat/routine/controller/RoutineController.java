package com.wechat.routine.controller;

import com.wechat.routine.common.req.RoutineFaultWorkReq;
import com.wechat.routine.pojo.RoutineFaultWork;
import com.wechat.routine.pojo.RoutineMaintainer;
import com.wechat.routine.utils.ApiResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RoutineController {

    /**
     * 用户登陆
     */
    public ApiResponse loginUser(@RequestBody RoutineFaultWorkReq req){
        return ApiResponse.ofSuccess(null);
    }

    /**
     * 添加报障信息
     */
    public ApiResponse addFailure(@RequestBody RoutineFaultWorkReq req){
        return ApiResponse.ofMessage(null);
    }


    /**
     * 根据手机号查询报障记录
     */
    public ApiResponse getUserFailureRecord(@RequestParam Integer phone){
        return ApiResponse.ofMessage(null);
    }

    /**
     * 获取工单处理进度
     */
    public ApiResponse getWorkSchedule(@RequestParam String workId){
        return ApiResponse.ofMessage(null);
    }

    /**'
     * 查看报障信息
     */
    public ApiResponse getUserFailureInfo(@RequestParam String workId){
        return ApiResponse.ofMessage(null);
    }

    /**
     * 修改报障信息
     */
    public ApiResponse updateUserFailureInfo(@RequestBody RoutineFaultWork faultWork){
        return ApiResponse.ofMessage(null);
    }

    /**
     * 获取维护人员信息
     */
    public ApiResponse getMaintainerInfo(@RequestParam String maintainer){
        return ApiResponse.ofMessage(null);
    }

    /**
     * 根据类型查询所有工单
     */
    public ApiResponse getAllFaultWorkByType(@RequestParam String type){
        return ApiResponse.ofMessage(null);
    }

    /**
     * 获取维护人员列表
     */
    public ApiResponse getMaintainer(){
        return null;
    }

    /**
     * 获取该人员消息通知
     */
    public ApiResponse getUserNotice(){
        return null;
    }

    /**
     * 维护人员登陆
     */
    public ApiResponse loginMaintainer(){
        return null;
    }

    /**
     * 满意度问卷
     */
    public ApiResponse addQuis(){
        return null;
    }



}
