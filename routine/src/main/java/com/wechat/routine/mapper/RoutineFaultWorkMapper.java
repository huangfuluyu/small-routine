package com.wechat.routine.mapper;

import com.wechat.routine.pojo.RoutineFaultWork;
import com.wechat.routine.pojo.RoutineMaintainer;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Mapper
@Component
public interface RoutineFaultWorkMapper {


    int insert(RoutineFaultWork row);

    RoutineFaultWork selectByPrimaryKey(Integer id);

    int updateByPrimaryKey(RoutineFaultWork row);

    int updateWorkSateByPrimaryKey(RoutineFaultWork row);

    List<RoutineFaultWork> selectByUserPhone(Long userPhone);

    List<RoutineFaultWork> selectAllByWorkStateAndId(Integer workState, Integer id);
    List<RoutineFaultWork> selectAllByWorkConfirmAndId(Integer workConfirm, Integer id);
    List<RoutineFaultWork> selectAllByWorkStateAndOldId(Integer id);

    RoutineFaultWork selectByWorkId(String workId);

    int selectCount(String toDay);

    int updateConFirmWork(RoutineFaultWork row);

    int updateById(RoutineFaultWork row);

    List<RoutineFaultWork> selectAll();

    int updateByPunch(RoutineFaultWork row);
    int updateByScoreState(String workId);


}