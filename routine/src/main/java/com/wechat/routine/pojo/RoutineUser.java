package com.wechat.routine.pojo;

import com.wechat.routine.common.BaseTable;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = true)
public class RoutineUser extends BaseTable {
    public String userName;

    public Short userGender;

    public Integer userPhone;

    public String userUnit;

    private static final long serialVersionUID = 1L;

}