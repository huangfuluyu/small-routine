package com.wechat.routine.pojo;

import com.wechat.routine.common.BaseTable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel("用户消息")
public class RoutineMessage extends BaseTable {

    public String workId;

    @ApiModelProperty("id")
    public Long userId;
    /**
     * 1客户 2维护人员 3管理员
     */
    @ApiModelProperty("1客户 2维护人员 3管理员")
    public Integer userType;

    @ApiModelProperty("用户\n" +
            "1，用户发起工单\n" +
            "2. 维护人员接收\n" +
            "3. 维修完成\n" +
            "4. 评价生成\n"+
            "维护人员\n"+
            "1. 接到后台分配的订单，提醒一次\n" +
            "2. 未接单的一个小时提醒一次\n"
            )
    public Integer messages;

    @ApiModelProperty("0：未读 1：已读")
    public Integer readState;
}
