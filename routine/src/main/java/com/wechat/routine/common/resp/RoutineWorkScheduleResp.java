package com.wechat.routine.common.resp;

import lombok.Data;

import java.util.List;

/**
 * @author : HuangFu
 * @Description : RoutineWorkScheduleResp
 * @date : 2022-05-25 14:25
 **/
@Data
public class RoutineWorkScheduleResp {

    public String workId;

    public List<String> schedule;

}
