package com.wechat.routine.controller;

import com.ctc.wstx.util.StringUtil;
import com.wechat.routine.common.Constant;
import com.wechat.routine.common.req.RoutineFaultWorkReq;
import com.wechat.routine.config.JwtConfig;
import com.wechat.routine.enums.ExceptionEnum;
import com.wechat.routine.mapper.RoutineFaultWorkMapper;
import com.wechat.routine.mapper.RoutineMaintainerMapper;
import com.wechat.routine.mapper.RoutineUserMapper;
import com.wechat.routine.mapper.RoutineWorkScheduleMapper;
import com.wechat.routine.pojo.RoutineFaultWork;
import com.wechat.routine.pojo.RoutineMaintainer;
import com.wechat.routine.pojo.RoutineUser;
import com.wechat.routine.pojo.RoutineWorkSchedule;
import com.wechat.routine.utils.ApiResponse;
import lombok.val;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@RestController
public class RoutineController {

    @Autowired
    RoutineUserMapper routineUserMapper;

    @Resource
    private JwtConfig jwtConfig;

    @Autowired
    RoutineFaultWorkMapper routineFaultWorkMapper;

    @Autowired
    RoutineWorkScheduleMapper routineWorkScheduleMapper;

    @Autowired
    RoutineMaintainerMapper routineMaintainerMapper;

    /**
     * 维护人员登陆
     */
    @PostMapping("/login")
    public ApiResponse loginUser(@RequestBody RoutineFaultWorkReq req) {
        return null;
    }

    /**
     * 添加报障信息
     */
    @PostMapping("/addFailure")
    public ApiResponse addFailure(@RequestBody RoutineFaultWorkReq req) {

        //创建token
        RoutineUser user = new RoutineUser();
        BeanUtils.copyProperties(req, user);
        String token = jwtConfig.createToken(req.userName + req.userPhone);
        user.setToken(token);
        routineUserMapper.insert(user);

//        val routineUser = routineUserMapper.selectByUserPhoneAndUserNameRoutineUser(user);
        //不存在创建用户,存在
//        if(routineUser==null){
//            String token = jwtConfig.createToken(req.userName + req.userPhone);
//            user.setToken(token);
//            routineUserMapper.insert(user);
//        }else {
//            routineUserMapper.updateByPrimaryKey(routineUser);
//        }
        //插入工单
        RoutineFaultWork faultWork = new RoutineFaultWork();
        BeanUtils.copyProperties(req, faultWork);
        int workId = routineFaultWorkMapper.insert(faultWork);

        //插入工单进度表
        RoutineWorkSchedule schedule = new RoutineWorkSchedule();
        List<String> list = new ArrayList<>();
        list.add(Constant.ONE_SCHEDULE);
        list.add(Constant.TWO_SCHEDULE);
        schedule.setWorkId(String.valueOf(workId));
        schedule.setSchedule(list.toString());
        routineWorkScheduleMapper.insert(schedule);


        return ApiResponse.ofStatus(ExceptionEnum.OK);
    }

    /**
     * 根据手机号查询报障记录
     */
    @PostMapping("getUserFailureRecord")
    private ApiResponse getUserFailureRecord(@RequestParam Integer phone) {
        val userFaultWork = routineFaultWorkMapper.selectByUserPhone(phone);
        if (userFaultWork == null) {
            return ApiResponse.ofMessage(null);
        }
        return ApiResponse.ofSuccess(userFaultWork);
    }


    /**
     * 获取工单处理进度
     */
    public ApiResponse getWorkSchedule(@RequestParam String workId) {
        return ApiResponse.ofMessage(null);
    }

    /**
     * '
     * 查看报障信息
     */
    public ApiResponse getUserFailureInfo(@RequestParam String workId) {
        return ApiResponse.ofMessage(null);
    }

    /**
     * 修改报障信息
     */
    public ApiResponse updateUserFailureInfo(@RequestBody RoutineFaultWork faultWork) {
        return ApiResponse.ofMessage(null);
    }

    /**
     * 获取维护人员信息
     */
    public ApiResponse getMaintainerInfo(@RequestParam String maintainer) {
        return ApiResponse.ofMessage(null);
    }

    /**
     * 根据类型查询所有工单
     */
    public ApiResponse getAllFaultWorkByType(@RequestParam String type) {
        return ApiResponse.ofMessage(null);
    }

    /**
     * 获取维护人员列表
     */
    public ApiResponse getMaintainer() {
        return null;
    }

    /**
     * 获取该人员消息通知
     */
    public ApiResponse getUserNotice() {
        return null;
    }

    /**
     * 维护人员登陆
     */
    public ApiResponse loginMaintainer() {
        return null;
    }

    /**
     * 满意度问卷
     */
    public ApiResponse addQuis() {
        return null;
    }


}
