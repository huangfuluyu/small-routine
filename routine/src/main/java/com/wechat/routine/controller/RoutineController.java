package com.wechat.routine.controller;

import com.ctc.wstx.util.StringUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.wechat.routine.common.Constant;
import com.wechat.routine.common.req.RoutineFaultWorkReq;
import com.wechat.routine.common.resp.RoutineFaultWorkResp;
import com.wechat.routine.common.resp.RoutineWorkScheduleResp;
import com.wechat.routine.config.JwtConfig;
import com.wechat.routine.enums.ExceptionEnum;
import com.wechat.routine.mapper.*;
import com.wechat.routine.pojo.*;
import com.wechat.routine.utils.ApiResponse;
import com.wechat.routine.utils.PageRequest;
import com.wechat.routine.utils.PageUtil;
import com.wechat.routine.utils.ShiroKit;
import io.jsonwebtoken.Claims;
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
import java.security.SignatureException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

    @Autowired
    UserMapper userMapper;


    public ApiResponse getToken(@RequestParam("userName") String userName,
                                @RequestParam("passWord") String passWord){
        return null;
    }

    /**
     * 维护人员登陆
     */
    @PostMapping("/login")
    public ApiResponse loginUser(@RequestParam String userName,@RequestParam String password) {

        User user = userMapper.selectByAccount(userName);

        if(user != null){
            String mdPassWord = ShiroKit.md5(password,user.getSalt());
            if(user.getPassword().equals(mdPassWord)){
                String token = jwtConfig.createToken(userName);
                return ApiResponse.ofStatus(ExceptionEnum.OK,token);
            }
        }
        return ApiResponse.ofStatus(ExceptionEnum.LOGIN_ERROR);
    }

    /**
     * 添加报障信息
     */
    @PostMapping("/addFailure")
    public ApiResponse addFailure(@RequestBody RoutineFaultWorkReq req) {

        //创建token
        RoutineUser user = new RoutineUser();
        BeanUtils.copyProperties(req, user);
//        String token = jwtConfig.createToken(req.userName + req.userPhone);
//        user.setToken(token);
//        routineUserMapper.insert(user);

        val routineUser = routineUserMapper.selectByUserPhoneAndUserNameRoutineUser(user);
        //不存在创建用户,存在
        if(routineUser==null){
            String token = jwtConfig.createToken(req.getUserName() + req.getUserPhone());
            user.setToken(token);
            routineUserMapper.insert(user);
        }else {
            //存在更新
            String token = jwtConfig.createToken(routineUser.getId().toString());
            routineUser.setToken(token);
            routineUserMapper.updateByPrimaryKey(routineUser);
        }

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
    public ApiResponse getWorkSchedule(@RequestParam Long workId) {
        val workSchedule= routineWorkScheduleMapper.selectByWorkId(workId.intValue());
        List<String> schedule =  Stream.of(workSchedule.getSchedule()).collect(Collectors.toList());
        RoutineWorkScheduleResp resp = new RoutineWorkScheduleResp();
        resp.setWorkId(workSchedule.getWorkId());
        resp.setSchedule(schedule);
        return ApiResponse.ofStatus(ExceptionEnum.OK,resp);
    }

    /**
     * '
     * 查看报障信息
     */
    public ApiResponse getUserFailureInfo(@RequestParam Long workId) {
        RoutineFaultWorkResp workResp = new RoutineFaultWorkResp();
        val work = routineFaultWorkMapper.selectByPrimaryKey(workId.intValue());
        BeanUtils.copyProperties(work,workResp);
        return ApiResponse.ofStatus(ExceptionEnum.OK,workResp);
    }

    /**
     * 修改报障信息
     */
    public ApiResponse updateUserFailureInfo(@RequestBody RoutineFaultWorkReq req) {
        RoutineFaultWork faultWork = new RoutineFaultWork();
        BeanUtils.copyProperties(req,faultWork);
        routineFaultWorkMapper.updateByPrimaryKey(faultWork);
        return ApiResponse.ofStatus(ExceptionEnum.OK);
    }

    /**
     * 获取维护人员信息
     */
    public ApiResponse getMaintainerInfo(@RequestParam String account) {

        return ApiResponse.ofMessage(null);
    }

    /**
     * 根据类型查询所有工单
     */
    public ApiResponse getAllFaultWorkByType(@RequestParam PageRequest pageRequest, @RequestParam Integer type) {
        int pageNum = pageRequest.getPageNum();
        int pageSize = pageRequest.getPageSize();
        PageHelper.startPage(pageNum,pageSize);
        val workList = routineFaultWorkMapper.selectAllByWorkState(type);
        return ApiResponse.ofSuccess(new PageInfo<>(workList));
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
