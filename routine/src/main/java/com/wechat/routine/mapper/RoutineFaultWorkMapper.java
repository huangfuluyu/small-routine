package com.wechat.routine.mapper;

import com.wechat.routine.pojo.RoutineFaultWork;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

@Mapper
@Component
public interface RoutineFaultWorkMapper {


    int insert(RoutineFaultWork row);

    int insertSelective(RoutineFaultWork row);

    RoutineFaultWork selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(RoutineFaultWork row);

    int updateByPrimaryKey(RoutineFaultWork row);
}