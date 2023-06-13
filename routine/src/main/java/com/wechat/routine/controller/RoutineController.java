package com.wechat.routine.controller;

import cn.hutool.core.date.DateUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSON;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.wechat.routine.common.Constant;
import com.wechat.routine.common.req.LoginReq;
import com.wechat.routine.common.req.PushQuisReq;
import com.wechat.routine.common.req.RoutineFaultWorkReq;
import com.wechat.routine.common.resp.RoutineFaultWorkResp;
import com.wechat.routine.common.resp.RoutineWorkScheduleResp;
import com.wechat.routine.config.JwtConfig;
import com.wechat.routine.enums.ExceptionEnum;
import com.wechat.routine.mapper.*;
import com.wechat.routine.pojo.*;
import com.wechat.routine.utils.ApiResponse;
import com.wechat.routine.utils.ShiroKit;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
@Slf4j
@RequestMapping("/hf")
@Api("小程序接口管理")
public class RoutineController {
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd/");

    @Value("${spring.servlet.multipart.location}")
    private String uploadPath;

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
    RoutineScoreMapper routineScoreMapper;

    @Autowired
    UserMapper userMapper;

    @Autowired
    DeptMapper deptMapper;

    @Autowired
    RoutineMessageMapper routineMessageMapper;

    @Autowired
    RoutineAreaPhoneMapper routineAreaPhoneMapper;

    /**
     * 维护人员登陆
     */
    @PostMapping("/login")
    @ApiOperation("维护人员登陆")
    public ApiResponse loginUser(@RequestBody LoginReq req) {

        User user = userMapper.selectByAccount(req.getUserName());

        if (user != null) {
            String mdPassWord = ShiroKit.md5(req.getPassword(), user.getSalt());
            if (user.getPassword().equals(mdPassWord)) {
                String token = jwtConfig.createToken(req.getUserName());
                JSONObject js = new JSONObject();
                js.set("token", token);
                js.set("id", user.getId());
                js.set("name",user.getName());
                return ApiResponse.ofStatus(ExceptionEnum.OK, js);
            }
        }
        log.info("------维护人员登录------" + req.getUserName());
        return ApiResponse.ofStatus(ExceptionEnum.LOGIN_ERROR);
    }

    /**
     * 添加报障信息
     */
    @PostMapping("/addFailure")
    @ApiOperation("添加保障信息")
    @Transactional(rollbackFor = Exception.class)
    public ApiResponse addFailure(@RequestBody RoutineFaultWorkReq req) {

        try {
            log.info("======开始添加报障======");
            //拒绝维护人员报障
            List<User> userList = userMapper.selectByAll();
            List<Long> listPhone = userList.stream().map(o -> Long.getLong(o.getPhone())).collect(Collectors.toList());
            if (listPhone.contains(req.getUserPhone())) {
                log.info("------拒绝维护人员登录------" + req.getUserPhone());
                return ApiResponse.ofStatus(ExceptionEnum.FORBIDDEN);
            }
            String resultToken;
            //创建token
            RoutineUser user = new RoutineUser();
            BeanUtils.copyProperties(req, user);
            Long userId;
            val routineUser = routineUserMapper.selectByUserPhoneRoutineUser(req.getUserPhone());
            //不存在创建用户,存在
            if (routineUser == null) {
                log.info("------创建新用户------" + req.getUserPhone());
                String token = jwtConfig.createToken(req.getUserName() + req.getUserPhone());
                user.setToken(token);
                routineUserMapper.insert(user);
                userId = user.getId();
                resultToken = token;
            } else {
                log.info("------存在用户更新token------" + req.getUserPhone());
                //存在更新
                userId = routineUser.getId();
                String token = jwtConfig.createToken(routineUser.getId().toString());
                routineUser.setToken(token);
                routineUserMapper.updateByPrimaryKey(routineUser);
                resultToken = token;
            }

            //插入工单
            int num = routineFaultWorkMapper.selectCount(DateUtil.today()) + 1;
            String workId = "HZ-ZX-" + DateUtil.today() + "-" + num;
            RoutineFaultWork faultWork = new RoutineFaultWork();
            BeanUtils.copyProperties(req, faultWork);
            faultWork.setUserPhone(req.getUserPhone());
            faultWork.setWorkId(workId);
            routineFaultWorkMapper.insert(faultWork);
            log.info("------插入工单------");

            //插入工单进度表
            RoutineWorkSchedule schedule = new RoutineWorkSchedule();
            List<String> list = new ArrayList<>();
            list.add(Constant.ONE_SCHEDULE);
            list.add(Constant.TWO_SCHEDULE);
            schedule.setWorkId(workId);
            schedule.setSchedule(StringUtils.join(list, ","));
            routineWorkScheduleMapper.insert(schedule);
            log.info("------插入工单进度表------");

            //插入客户通知消息,后台人员已接收，等待维护人员接单
            insertMessage(workId, userId, 1, 1);
            log.info("------通知用户：工单已接收------");

            //获取区级管理员
            //通知各位区级管理员
//        deptMapper.listBySimplenameDepts();
            String area = req.getFaultAddress().substring(6, 8);

            //也通知区级管理员，sql语句
            List<Dept> depts = deptMapper.listByFullNameDept(area);
            List<Long> deptIds = depts.stream().map(Dept::getId).collect(Collectors.toList());
            List<Integer> intDeptIds = deptIds.stream().map(Long::intValue).collect(Collectors.toList());
            List<User> users = userMapper.selectByDeptId(intDeptIds);
            users.forEach(o -> {
                RoutineMessage adminMessage = new RoutineMessage();
                adminMessage.setWorkId(workId);
                adminMessage.setUserId(o.getId());
                adminMessage.setUserType(3);
                adminMessage.setMessages(1);
                routineMessageMapper.insertRoutineMessage(adminMessage);
            });
            log.info("-------通知区级管理员-----");
            log.info("======报障结束======");
            return ApiResponse.ofStatus(ExceptionEnum.OK, resultToken);
        }catch (Exception e){
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * 根据手机号查询报障记录
     */
    @ApiOperation("根据手机号查询保障记录")
    @GetMapping("/getUserFailureRecord")
    public ApiResponse getUserFailureRecord(@RequestParam Long userPhone) {
        RoutineFaultWorkResp workResp = new RoutineFaultWorkResp();
        List<RoutineFaultWork> userFaultWorks = routineFaultWorkMapper.selectByUserPhone(userPhone);
        if (userFaultWorks == null || userFaultWorks.size()==0) {
            return ApiResponse.ofSuccess(new RoutineFaultWorkResp());
        }
        RoutineFaultWork userFaultWork = userFaultWorks.get(0);
        log.info("------查询报障记录------" + userPhone);
        BeanUtils.copyProperties(userFaultWork, workResp);
        return ApiResponse.ofSuccess(workResp);
    }

    public void insertMessage(String workId, Long userId, Integer userType, Integer messages) {
        //插入客户通知消息
        RoutineMessage routineMessage = new RoutineMessage();
        routineMessage.setWorkId(workId);
        routineMessage.setUserId(userId);
        routineMessage.setUserType(userType);
        routineMessage.setMessages(messages);
        routineMessageMapper.insertRoutineMessage(routineMessage);
    }


    /**
     * 获取工单处理进度
     */
    @GetMapping("/getWorkSchedule")
    @ApiOperation("获取工单处理进度")
    public ApiResponse getWorkSchedule(@RequestParam String workId) {

        val workSchedule = routineWorkScheduleMapper.selectByWorkId(workId);
        List<String> schedule = Stream.of(workSchedule.getSchedule()).collect(Collectors.toList());
        RoutineWorkScheduleResp resp = new RoutineWorkScheduleResp();
        resp.setWorkId(workSchedule.getWorkId());
        resp.setSchedule(schedule);

        RoutineFaultWork faultWork = routineFaultWorkMapper.selectByWorkId(workId);

        RoutineMaintainer maintainer = routineMaintainerMapper.selectByPrimaryKey(faultWork.getMaintainerId());
        if (maintainer != null) {
            resp.setName(maintainer.getMaintainerName());
            resp.setPhone(maintainer.getMaintainerPhone());
        }

        return ApiResponse.ofStatus(ExceptionEnum.OK, resp);
    }

    @ApiOperation("更新用户消息消息通知阅读状态")
    @PostMapping("/updateUserRead")
    public ApiResponse updateUserRead(@RequestBody Long phone) {
        RoutineUser routineUser = routineUserMapper.selectByUserPhone(phone);
        List<RoutineMessage> lists = routineMessageMapper.selectByIdAndType(routineUser.getId().intValue(), 1, 0);
        if (!lists.isEmpty()) {
            routineMessageMapper.UpdateBatchReadState(lists);
        }

        return ApiResponse.ofStatus(ExceptionEnum.OK);
    }

    @ApiOperation("更新维护人员消息通知阅读状态")
    @PostMapping("/updateMainRead")
    public ApiResponse updateMainRead(@RequestBody Integer id) {
        List<RoutineMessage> lists = routineMessageMapper.selectByIdAndType(id, 2, 0);
        if (!lists.isEmpty()) {
            routineMessageMapper.UpdateBatchReadState(lists);
        }
        return ApiResponse.ofStatus(ExceptionEnum.OK);
    }


    /**
     * '
     * 查看报障信息
     */
    @GetMapping("/getUserFailureInfo")
    @ApiOperation("查看报障信息")
    public ApiResponse getUserFailureInfo(@RequestParam String workId) {
        RoutineFaultWorkResp workResp = new RoutineFaultWorkResp();
        val work = routineFaultWorkMapper.selectByWorkId(workId);
        BeanUtils.copyProperties(work, workResp);
        return ApiResponse.ofStatus(ExceptionEnum.OK, workResp);
    }

    /**
     * 修改报障信息
     */
    @ApiOperation("修改报障信息")
    @PostMapping("/updateUserFailureInfo")
    @Transactional(rollbackFor = Exception.class)
    public ApiResponse updateUserFailureInfo(@RequestBody RoutineFaultWorkReq req) {
        RoutineFaultWork faultWork = new RoutineFaultWork();
        BeanUtils.copyProperties(req, faultWork);
        RoutineFaultWork faultWork1 = routineFaultWorkMapper.selectByWorkId(req.getWorkId());
        if (!Objects.equals(req.getUserPhone(), faultWork1.getUserPhone())) {
            RoutineUser routineUser = routineUserMapper.selectByUserPhone(faultWork1.getUserPhone());
            routineUser.setUserPhone(req.getUserPhone());
            routineUserMapper.updateByPrimaryKey(routineUser);
        }
        faultWork.setId(faultWork1.getId());
        routineFaultWorkMapper.updateByPrimaryKey(faultWork);
        log.info("------修改报障记录信息------");
        return ApiResponse.ofStatus(ExceptionEnum.OK);
    }

    @ApiOperation("维护人员确认收到工单")
    @PostMapping("/updateMainConfirmWork")
    @Transactional(rollbackFor = Exception.class)
    public ApiResponse mainConfirm(@RequestBody RoutineFaultWorkResp req) {

        RoutineFaultWork faultWork = routineFaultWorkMapper.selectByPrimaryKey(req.getId().intValue());
        if (faultWork.getWorkConfirm() == 1) {
            return ApiResponse.ofStatus(ExceptionEnum.OK);
        }
        BeanUtils.copyProperties(req, faultWork);
        routineFaultWorkMapper.updateConFirmWork(faultWork);

        val routineUser = routineUserMapper.selectByUserPhoneRoutineUser(req.getUserPhone());

        //通知客户维护人员已经接单,正在路上
        insertMessage(req.getWorkId(), routineUser.getId(), 1, 2);

        //更新工单进度表
        RoutineWorkSchedule routineWorkSchedule = routineWorkScheduleMapper.selectByWorkId(req.getWorkId());
        List<String> list = Stream.of(routineWorkSchedule.getSchedule()).collect(Collectors.toList());
        list.add("维护人员已接单.正在赶来的路上...");
        routineWorkSchedule.setSchedule(StringUtils.join(list, ","));
        routineWorkScheduleMapper.updateByPrimaryKey(routineWorkSchedule);

        routineMessageMapper.deleteByWorkIdAndUserIdAndType(req.getWorkId(), 3, 3);
        return ApiResponse.ofStatus(ExceptionEnum.OK);
    }

    @PostMapping("/workPunch")
    @ApiOperation("工单打卡")
    @Transactional(rollbackFor = Exception.class)
    public ApiResponse workPunch(@RequestBody RoutineFaultWorkResp req) {

        RoutineFaultWork faultWork = new RoutineFaultWork();
        BeanUtils.copyProperties(req, faultWork);
        RoutineFaultWork faultWork1 = routineFaultWorkMapper.selectByWorkId(req.getWorkId());
        Integer confirm = faultWork1.getWorkConfirm();
        if (faultWork1.getPunch() != null) {
            return ApiResponse.ofStatus(ExceptionEnum.OK);
        }
        faultWork.setId(faultWork1.getId());
        routineFaultWorkMapper.updateByPunch(faultWork);

        //更新工单进度表
        RoutineWorkSchedule routineWorkSchedule = routineWorkScheduleMapper.selectByWorkId(req.getWorkId());
        List<String> list = Stream.of(routineWorkSchedule.getSchedule()).collect(Collectors.toList());
        if (confirm == 1) {
            list.add("维护人员到达指定地点. 正在处理中...");
        } else {
            list.add("维护人员已接单.正在赶来的路上...");
            list.add("维护人员到达指定地点. 正在处理中...");
        }
        routineWorkSchedule.setSchedule(StringUtils.join(list, ","));
        routineWorkScheduleMapper.updateByPrimaryKey(routineWorkSchedule);
        return ApiResponse.ofStatus(ExceptionEnum.OK);

    }

    @ApiOperation("维护人员提交工单")
    @PostMapping("/updateMainSubmitWork")
    @Transactional(rollbackFor = Exception.class)
    public ApiResponse updateMainSubmitWork(@RequestBody RoutineFaultWorkResp req) {

        RoutineFaultWork faultWork = new RoutineFaultWork();
        BeanUtils.copyProperties(req, faultWork);
        RoutineFaultWork faultWork1 = routineFaultWorkMapper.selectByWorkId(req.getWorkId());
        int confirm = faultWork1.getWorkConfirm();
        faultWork.setOldMaintainerId(faultWork1.getOldMaintainerId());
        faultWork.setId(faultWork1.getId());
        faultWork.setWorkConfirm(1);
        faultWork.setWorkState(3);
        routineFaultWorkMapper.updateById(faultWork);

        //通知用户已经完成工单
        val routineUser = routineUserMapper.selectByUserPhoneRoutineUser(req.getUserPhone());
        insertMessage(req.getWorkId(), routineUser.getId(), 1, 3);
        //通知用户评价
        RoutineMessage routineMessage = new RoutineMessage();
        routineMessage.setWorkId(req.getWorkId());
        routineMessage.setUserId(routineUser.getId());
        routineMessage.setUserType(1);
        routineMessage.setMessages(4);
        routineMessage.setCreateTime(new Date(System.currentTimeMillis() + 5000));
        routineMessageMapper.insertRoutineMessageByTime(routineMessage);

        //更新工单进度表
        RoutineWorkSchedule routineWorkSchedule = routineWorkScheduleMapper.selectByWorkId(req.getWorkId());
        List<String> list = Stream.of(routineWorkSchedule.getSchedule()).collect(Collectors.toList());
        if (confirm == 1) {
            list.add("维修完成.工单报结");
            list.add("请对这次服务进行评价");
        } else {
            list.add("维护人员已接单.即将远程服务...");
            list.add("维修完成.工单报结");
            list.add("请对这次服务进行评价");
        }
        routineWorkSchedule.setSchedule(StringUtils.join(list, ","));
        routineWorkScheduleMapper.updateByPrimaryKey(routineWorkSchedule);

        return ApiResponse.ofStatus(ExceptionEnum.OK);
    }


    /**
     * 转单
     *
     * @return
     */
    @ApiOperation("维护人员转单")
    @Transactional(rollbackFor = Exception.class)
    @PostMapping(value = "/updateMainTransferWork")
    public ApiResponse updateMainTransferWork(@RequestBody RoutineFaultWorkResp req) {
        RoutineFaultWork faultWork = new RoutineFaultWork();
        BeanUtils.copyProperties(req, faultWork);
        RoutineFaultWork faultWork1 = routineFaultWorkMapper.selectByWorkId(req.getWorkId());
        faultWork.setId(faultWork1.getId());
        faultWork.setOldMaintainerId(faultWork1.getMaintainerId());
        faultWork.setMaintainerId(null);
        //工单状态为2
        faultWork.setWorkState(2);
        routineFaultWorkMapper.updateById(faultWork);

        //向管理员发送消息
        RoutineMessage adminMessage = new RoutineMessage();
        adminMessage.setWorkId(req.getWorkId());
        adminMessage.setUserId(req.getAdminId().longValue());
        adminMessage.setUserType(3);
        adminMessage.setMessages(2);
        routineMessageMapper.insertRoutineMessage(adminMessage);

        return ApiResponse.ofStatus(ExceptionEnum.OK);
    }

    /**
     * 根据类型查询所有工单
     */
    @ApiOperation("根据类型查询所有工单")
    @GetMapping("/getAllFaultWorkByType")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "type", value = "工单类型：1：待办 2：转单 3：已办 "),
            @ApiImplicitParam(name = "mainId", value = "维护人员id"),
    })
    public ApiResponse getAllFaultWorkByType(@RequestParam Integer pageNum, @RequestParam Integer pageSize, @RequestParam Integer type, @RequestParam Integer mainId) {

        PageHelper.startPage(pageNum, pageSize);

        List<RoutineFaultWork> workList;

        if (type == 0) {
            try {
                workList = routineFaultWorkMapper.selectAllByWorkStateAndId(type, mainId);
            } catch (NullPointerException e) {
                return ApiResponse.ofSuccess(new PageInfo<>());
            }
        } else if (type == 2) {
            try {
                workList = routineFaultWorkMapper.selectAllByWorkStateAndOldId(mainId);
            } catch (NullPointerException e) {
                return ApiResponse.ofSuccess(new PageInfo<>());
            }
        } else {
            try {
                workList = routineFaultWorkMapper.selectAllByWorkStateAndId(type, mainId);
            } catch (NullPointerException e) {
                return ApiResponse.ofSuccess(new PageInfo<>());
            }
        }

        List<RoutineFaultWorkResp> result = Lists.newArrayList();
        workList.forEach(o->{
            RoutineFaultWorkResp resp = new RoutineFaultWorkResp();
            BeanUtils.copyProperties(o,resp);
            result.add(resp);
        });
        return ApiResponse.ofSuccess(new PageInfo<>(result));
    }

    /**
     * 获取维护人员列表
     */
    @ApiOperation("获取维护人员列表")
    @GetMapping("/getMaintainer")
    public ApiResponse getMaintainer(@RequestParam Integer pageNum, @RequestParam Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<RoutineMaintainer> result;
        try {
            result = routineMaintainerMapper.selectAll();
        } catch (NullPointerException e) {
            return ApiResponse.ofSuccess(new PageInfo<>());
        }
        return ApiResponse.ofSuccess(new PageInfo<>(result));
    }

    /**
     * 获取上级管理员列表
     *
     * @return
     */
    @ApiOperation("获取上级管理员列表")
    @GetMapping("/getLeaders")
    @ApiImplicitParam(name = "maintainerId", value = "维护人员id")
    public ApiResponse getLeaders(@RequestParam Integer maintainerId) {
        RoutineMaintainer maintainer = routineMaintainerMapper.selectByPrimaryKey(maintainerId);
        if(maintainer!=null){
            List<Integer> leaderIds = Stream.of(maintainer.getLeaderIds().replace(" ", "").split(",")).map(Integer::valueOf).collect(Collectors.toList());
            List<User> users = userMapper.selectAllByIdUsers(leaderIds);
            return ApiResponse.ofStatus(ExceptionEnum.OK, users);
        }
        return ApiResponse.ofStatus(ExceptionEnum.OK,new User());
    }

    /**
     * 获取上级管理员列表
     *
     * @return
     */
    @ApiOperation("获取市级管理员列表")
    @GetMapping("/getCityLeaders")
    public ApiResponse getLeaders() {
        //市级管理员id
        List<User> users = userMapper.selectByRoleIdUsers(6);
        return ApiResponse.ofStatus(ExceptionEnum.OK, users);

    }

    /**
     * 获取用户消息通知
     */
    @ApiOperation("获取用户消息通知列表")
    @GetMapping("/getUserMessage")
    public ApiResponse getUserMessage(@RequestParam Long phone, @RequestParam Integer pageNum, @RequestParam Integer pageSize, @RequestParam Integer readState) {
        RoutineUser routineUser = routineUserMapper.selectByUserPhone(phone);
        PageHelper.startPage(pageNum, pageSize);
        List<RoutineMessage> lists;
        try {
            lists = routineMessageMapper.selectByIdAndType(routineUser.getId().intValue(), 1, readState);
        } catch (NullPointerException e) {
            return ApiResponse.ofSuccess(new PageInfo<>());
        }

        return ApiResponse.ofSuccess(new PageInfo<>(lists));
    }


    /**
     * 获取维护人员消息通知
     */
    @ApiOperation("获取维护人员消息通知列表")
    @GetMapping("/getMainMessage")
    public ApiResponse getMainMessage(@RequestParam Long id, @RequestParam Integer pageNum, @RequestParam Integer pageSize, @RequestParam Integer readState) {

        PageHelper.startPage(pageNum, pageSize);

        List<RoutineMessage> lists;
        try {
            lists = routineMessageMapper.selectByIdAndType(id.intValue(), 2, readState);
        } catch (NullPointerException e) {
            return ApiResponse.ofSuccess(new PageInfo<>());
        }

        return ApiResponse.ofSuccess(new PageInfo<>(lists));
    }

    /**
     * 满意度问卷
     */
    @ApiOperation("满意度问卷")
    @PostMapping("/addQuis")
    @Transactional(rollbackFor = Exception.class)
    public ApiResponse addQuis(@RequestBody RoutineScore routineScore) {

        if (!routineScore.getWorkId().equals("")) {
            val faultWork = routineFaultWorkMapper.selectByWorkId(routineScore.getWorkId());
            if (faultWork != null) {
                if (faultWork.getWorkScore() == 1) {
                    return ApiResponse.of(-100, "请勿重复评价！", null);
                }
            }
        }

        if(routineScore.getUserId()!=null){
            RoutineUser user = routineUserMapper.selectByPrimaryKey(routineScore.getUserId());
            System.out.println(user.getUserUnit());
            if(user!=null){
                routineScore.setUnitName(user.getUserUnit());
            }
        }

        routineScoreMapper.insertRoutineScore(routineScore);
        if (!routineScore.getWorkId().equals("")) {
            routineFaultWorkMapper.updateByScoreState(routineScore.getWorkId());
            routineMessageMapper.deleteByWorkId(routineScore.getWorkId());
        }

        return ApiResponse.ofStatus(ExceptionEnum.OK);
    }

//    @ApiOperation("文件上传")
//    @PostMapping(value = "/local", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
//    public Dict local(@RequestParam("file") MultipartFile file) {
//        if (file.isEmpty()) {
//            return Dict.create().set("code", 400).set("message", "文件内容为空");
//        }
//        String fileName = file.getOriginalFilename();
//        String rawFileName = StrUtil.subBefore(fileName, ".", true);
//        String fileType = StrUtil.subAfter(fileName, ".", true);
//        String localFilePath = StrUtil.appendIfMissing(fileTempPath, "/") + rawFileName + "-" + DateUtil.current(false) + "." + fileType;
//        try {
//            file.transferTo(new File(localFilePath));
//        } catch (IOException e) {
//            log.error("【文件上传至本地】失败，绝对路径：{}", localFilePath);
//            return Dict.create().set("code", 500).set("message", "文件上传失败");
//        }
//
//        log.info("【文件上传至本地】绝对路径：{}", localFilePath);
//        return Dict.create().set("code", 200).set("message", "上传成功").set("data", Dict.create().set("fileName", fileName).set("filePath", localFilePath));
//    }


    @ApiOperation("文件上传")
    @PostMapping("/local")
    public ApiResponse upload(MultipartFile uploadFile,
                              HttpServletRequest request) {
          String host = "https://hzzxzj.top";
//        String host = "https://59v57235h5.oicp.vip";
//        String host = "https://4159k5886f.zicp.vip";
        // 在 uploadPath 文件夹中通过日期对上传的文件归类保存
        // 比如：/2019/06/06/cf13891e-4b95-4000-81eb-b6d70ae44930.png
        String format = sdf.format(new Date());
        File folder = new File(uploadPath + format);
        if (!folder.isDirectory()) {
            folder.mkdirs();
        }

        // 对上传的文件重命名，避免文件重名
        String oldName = uploadFile.getOriginalFilename();
        String newName = UUID.randomUUID()
                + oldName.substring(oldName.lastIndexOf("."), oldName.length());
        try {
            // 文件保存
            uploadFile.transferTo(new File(folder, newName));

            // 返回上传文件的访问路径
//            String filePath = request.getScheme() + "://" + request.getServerName()
//                    + ":" + request.getServerPort() + "/" + format + newName;
            String filePath = host + "/" + format + newName;
            return ApiResponse.ofStatus(ExceptionEnum.OK, filePath);
        } catch (Exception e) {
            throw new RuntimeException();
        }

    }

    @ApiOperation("获取accessToken")
    @PostMapping(value = "/getAccessToken")
    private String getAccessToken() {
        String token;
        try {
            HashMap<String, Object> map = new HashMap<>();
            map.put("appid", "wx88809eee439677ce");
            map.put("grant_type", "client_credential");
            map.put("secret", "b7c21009d764b54b755edc9a23976f89");
            token = HttpUtil.get("https://api.weixin.qq.com/cgi-bin/token", map);
            JSON json = JSONUtil.parse(token);
            token = json.getByPath("access_token").toString();
        } catch (Exception e) {
            throw new RuntimeException();
        }
        return token;
    }

    @ApiOperation("生成工单评价页面")
    @PostMapping(value = "/pushQuis")
    public ApiResponse pushQuis(@RequestBody PushQuisReq req) {
        byte[] bodyByte;
        String result;
        try {
            String token = getAccessToken();
            JSONObject jsonObject = new JSONObject();
            jsonObject.set("page", req.getPage());
            jsonObject.set("scene", req.getScene());
            HttpResponse response = HttpRequest.post("https://api.weixin.qq.com/wxa/getwxacodeunlimit?access_token=" + token)
                    .body(jsonObject.toString())
                    .timeout(20000)
                    .execute();
            bodyByte = response.bodyBytes();
            result = Base64.getEncoder().encodeToString(bodyByte);
        } catch (Exception e) {
            throw new RuntimeException();
        }
        return ApiResponse.ofStatus(ExceptionEnum.OK, result);
    }

    @ApiOperation("生成App评价页面")
    @PostMapping(value = "/pushAppQuis")
    public ApiResponse pushAppQuis(@RequestBody PushQuisReq req) {
        byte[] bodyByte;
        String result;
        try {
            String token = getAccessToken();
            JSONObject jsonObject = new JSONObject();
            jsonObject.set("path", req.getPath());
            HttpResponse response = HttpRequest.post("https://api.weixin.qq.com/wxa/getwxacode?access_token=" + token)
                    .body(jsonObject.toString())
                    .timeout(20000)
                    .execute();
            bodyByte = response.bodyBytes();
            result = Base64.getEncoder().encodeToString(bodyByte);
        } catch (Exception e) {
            throw new RuntimeException();
        }
        return ApiResponse.ofStatus(ExceptionEnum.OK, result);
    }

    @ApiOperation("查询区县电话")
    @GetMapping(value = "/queryAreaPhoneAll")
    public ApiResponse queryAreaPhoneAll(){
        List<RoutineAreaPhone> result;
        try {
            result = routineAreaPhoneMapper.selectAll();
        }catch (Exception e){
            return ApiResponse.ofSuccess(new ArrayList<>());
        }
        return ApiResponse.ofSuccess(result);
    }
}
