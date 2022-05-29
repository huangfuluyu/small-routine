package com.wechat.routine.common.resp;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author : HuangFu
 * @Description : RoutineWorkScheduleResp
 * @date : 2022-05-25 14:25
 **/
@Data
@ApiModel("用户处理进度")
public class RoutineWorkScheduleResp implements Serializable {

    @ApiModelProperty("工单号")
    public String workId;

    @ApiModelProperty("进度")
    public List<String> schedule;

}
