package com.wechat.routine.mapper;

import com.wechat.routine.pojo.RoutineUser;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Mapper
@Component
public interface RoutineUserMapper {

    int deleteByPrimaryKey(Integer id);

    int insert(RoutineUser row);

    int insertSelective(RoutineUser row);

    RoutineUser selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(RoutineUser row);

    int updateByPrimaryKey(RoutineUser row);

    RoutineUser selectByUserPhoneAndUserNameRoutineUser(RoutineUser row);
}