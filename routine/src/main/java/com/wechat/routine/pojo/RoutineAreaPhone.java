package com.wechat.routine.pojo;

import com.wechat.routine.common.BaseTable;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author HuangFuLuYu
 * @date 2022/10/18/18:25
 * @Description
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class RoutineAreaPhone extends BaseTable {
    private String areaName;
    private String phoneNumber;
}
