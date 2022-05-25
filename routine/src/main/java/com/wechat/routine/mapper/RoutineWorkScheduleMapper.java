package com.wechat.routine.mapper;

import com.wechat.routine.pojo.RoutineWorkSchedule;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

@Mapper
@Component
public interface RoutineWorkScheduleMapper {


    int deleteByPrimaryKey(Integer id);

    int insert(RoutineWorkSchedule row);

    int insertSelective(RoutineWorkSchedule row);

    RoutineWorkSchedule selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(RoutineWorkSchedule row);

    int updateByPrimaryKey(RoutineWorkSchedule row);

    RoutineWorkSchedule selectByWorkId(Integer workId);
}