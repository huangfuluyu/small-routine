package com.wechat.routine.pojo;

import com.wechat.routine.common.BaseTable;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = true)
public class RoutineWorkSchedule extends BaseTable {

    public String workId;

    public String schedule;

    private static final long serialVersionUID = 1L;

}