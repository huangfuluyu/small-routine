package com.wechat.routine.pojo;

import com.wechat.routine.common.BaseTable;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)

public class RoutineMaintainer extends BaseTable {

    private String leaderIds;

    private String maintainerName;

    private Long maintainerPhone;

    private String maintainerAccount;

    private String maintainerPwd;

    private String token;

    private static final long serialVersionUID = 1L;

}