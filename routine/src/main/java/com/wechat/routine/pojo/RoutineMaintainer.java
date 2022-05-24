package com.wechat.routine.pojo;

import com.wechat.routine.common.BaseTable;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = true)
public class RoutineMaintainer extends BaseTable {
    private String maintainerName;

    private Integer maintainerPhone;

    private String maintainerAccount;

    private String maintainerPwd;

    private static final long serialVersionUID = 1L;

}