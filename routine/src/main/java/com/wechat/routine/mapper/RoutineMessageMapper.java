package com.wechat.routine.mapper;

import com.wechat.routine.pojo.RoutineMessage;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Mapper
@Component
public interface RoutineMessageMapper {
    int insertRoutineMessage(RoutineMessage routineMessage);

    int insertRoutineMessageByTime(RoutineMessage routineMessage);

    List<RoutineMessage> selectByIdAndType(Integer id,Integer type,Integer readState);

    int UpdateBatchReadState(List<RoutineMessage> list);

    RoutineMessage selectByWorkIdAndUserIdAndType(String workId,Integer id, Integer type,Integer messages);

    int deleteByWorkId(String workId);

    int deleteByWorkIdAndUserIdAndType(String workId, Integer userType,Integer messages);

}
