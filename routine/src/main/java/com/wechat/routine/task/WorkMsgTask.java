package com.wechat.routine.task;

import cn.hutool.core.date.DateUtil;
import com.wechat.routine.mapper.RoutineFaultWorkMapper;
import com.wechat.routine.mapper.RoutineMessageMapper;
import com.wechat.routine.pojo.RoutineFaultWork;
import com.wechat.routine.pojo.RoutineMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @author HuangFuLuYu
 * @date 2022/06/06/23:02
 * @Description
 */
@Slf4j
@Component
public class WorkMsgTask {

    @Autowired
    RoutineFaultWorkMapper routineFaultWorkMapper;

    @Autowired
    RoutineMessageMapper routineMessageMapper;

    @Scheduled(cron = "*/20 * * * * ?")
    public void task() {

        //查询下发维护人员但未确认的工单
        List<RoutineFaultWork> list = routineFaultWorkMapper.selectAll();
        if (!list.isEmpty()) {
            list.forEach(o -> {
                RoutineMessage main = routineMessageMapper.selectByWorkIdAndUserIdAndType(o.getWorkId(), o.getMaintainerId(), 2, 2);
                RoutineMessage admin = routineMessageMapper.selectByWorkIdAndUserIdAndType(o.getWorkId(), o.getAdminId(), 3, 3);
                Date issuedTime = o.getDispatchTime();
                long ms = DateUtil.betweenMs(issuedTime, DateUtil.date());

                if (ms >= 3600000) {
                    if (main == null) {
                        log.info("---------------超时1小时，通知维护人员---------------");
                        insertMessage(o.getWorkId(), o.getMaintainerId().longValue(), 2, 2);
                    }
                    if (ms > 7200000) {
                        if (admin == null) {
                            log.info("---------------超时2小时，通知管理员---------------");
                            insertMessage(o.getWorkId(), o.getAdminId().longValue(), 3, 3);
                        }
                    }
                }
            });
        }
    }

    public static void main(String[] args) {
        String dateStr = "2022-06-15 13:00:00";
        Date date = DateUtil.parse(dateStr);
        System.out.println("当前时间：" + date);
        Date date1 = DateUtil.offsetHour(date, 1);
        System.out.println("两个小时后时间：" + date1);
        long ms = DateUtil.betweenMs(date, date1);
        System.out.println("相差毫秒" + ms);

        long mss = DateUtil.betweenMs(date, DateUtil.date());
        System.out.println(mss);
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


}
