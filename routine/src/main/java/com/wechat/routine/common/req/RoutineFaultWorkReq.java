package com.wechat.routine.common.req;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@ApiModel(value = "工单请求参数")
public class RoutineFaultWorkReq implements Serializable {
    private static final long serialVersionUID = 5057954049311281252L;
    /**
     * 用户单位
     */
    @ApiModelProperty("用户单位")
    public String userUnit;

    /**
     * 用户名
     */
    @ApiModelProperty("客户名称")
    public String userName;

    /**
     * 性别
     */
    @ApiModelProperty("用户性别")
    public String userGender;

    /**
     * 电话
     */
    @ApiModelProperty("电话")
    public Integer userPhone;

    /**
     * 故障描述
     */
    @ApiModelProperty("故障描述")
    public String faultInfo;

    /**
     * 故障图片
     */
    @ApiModelProperty("故障图片")
    public String faultImg;

    /**
     * 工单创建时间
     */
    @ApiModelProperty("工单创建时间")
    public Date workTime;

    /**
     * 地址
     */
    @ApiModelProperty("地址")
    public String faultAddress;

    /**
     * 详细地址
     */
    @ApiModelProperty("详细地址")
    public String faultDetailAddress;

    /**
     * 备注
     */
    @ApiModelProperty("备注")
    public String remarks;
}
