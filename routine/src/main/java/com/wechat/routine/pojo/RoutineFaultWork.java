package com.wechat.routine.pojo;

import com.wechat.routine.common.BaseTable;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = true)
public class RoutineFaultWork extends BaseTable {

    public String userUnit;

    public String userName;

    public String userGender;

    public String userPhone;
    public String faultInfo;

    public String faultImg;

    public Date workTime;

    public String faultAddress;

    public String faultDetailAddress;

    public String questionInfo;
    public String solveImg;

    public String maintainer;

    public Integer maintainerPhone;

    public String remarks;

    public String door;

    public String punch;

    public Integer workDealWithStatus;

    public Integer workState;

    public static final long serialVersionUID = 1L;

}