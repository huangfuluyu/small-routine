package com.wechat.routine.common.req;


import lombok.Data;

import java.util.Date;

@Data
public class RoutineFaultWorkReq {
    /**
     * 用户单位
     */
    public String userUnit;

    /**
     * 用户名
     */
    public String userName;

    /**
     * 性别
     */
    public String userGender;

    /**
     * 电话
     */
    public String userPhone;

    /**
     * 故障描述
     */
    public String faultInfo;

    /**
     * 故障图片
     */
    public String faultImg;

    /**
     * 工单创建时间
     */
    public Date workTime;

    /**
     * 地址
     */
    public String faultAddress;

    /**
     * 详细地址
     */
    public String faultDetailAddress;

    /**
     * 备注
     */
    public String remarks;
}
