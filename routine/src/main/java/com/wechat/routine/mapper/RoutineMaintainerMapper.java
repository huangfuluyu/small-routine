package com.wechat.routine.mapper;

import com.wechat.routine.pojo.RoutineMaintainer;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Mapper
@Component
public interface RoutineMaintainerMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(RoutineMaintainer row);

    RoutineMaintainer selectByPrimaryKey(Integer id);

    int updateByPrimaryKey(RoutineMaintainer row);

    List<RoutineMaintainer> selectAll();
}