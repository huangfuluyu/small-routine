package com.wechat.routine.common.resp;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.wechat.routine.common.BaseTable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

@Data
@ApiModel("保障信息反参")
public class RoutineFaultWorkResp implements Serializable {


    @ApiModelProperty("id")
    public Long id;
    @ApiModelProperty("工单号")
    public String workId;
    @ApiModelProperty("单位")
    public String userUnit;
    @ApiModelProperty("客户名称")
    public String userName;
    @ApiModelProperty("性别")
    public String userGender;
    @ApiModelProperty("电话")
    public Long userPhone;
    @ApiModelProperty("故障描述")
    public String faultInfo;
    @ApiModelProperty("故障图片")
    public String faultImg;
    @ApiModelProperty("工单创建时间")

    public Date workTime;
    @ApiModelProperty("地址")
    public String faultAddress;
    @ApiModelProperty("详细地址")
    public String faultDetailAddress;
    @ApiModelProperty("问题说明")
    public String questionInfo;
    @ApiModelProperty("解决故障图片")
    public String solveImg;
    @ApiModelProperty("维护人员Id")
    public Integer maintainerId;
    @ApiModelProperty("维护人员手机号")
    public String maintainer;
    @ApiModelProperty("维护人员")
    public Integer maintainerPhone;
    @ApiModelProperty("备注")
    public String remarks;
    @ApiModelProperty("是否上门（0 不上门,1上门）")
    public String door;
    @ApiModelProperty("点位打卡位置")
    public String punch;
    @ApiModelProperty("确认工单: 未确认：0 已确认：1")
    public Integer workConfirm;
    @ApiModelProperty("工单处理状态（未解决：0 已解决：1）")
    public Integer workDealWithStatus;
    @ApiModelProperty("工单状态（未下单 0 待办 1转派 2，已办 3）")
    public Integer workState;
    @ApiModelProperty("评价状态 0 未评价 1已评价")
    public Integer workScore;

    @ApiModelProperty("转单前维护人员id")
    public Integer old_maintainer_id;

    @ApiModelProperty("后台管理员id")
    public Integer adminId;

    public static final long serialVersionUID = 1L;

}