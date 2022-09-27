package com.wechat.routine.mapper;

import com.wechat.routine.pojo.RoutineUser;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

@Mapper
@Component
public interface RoutineUserMapper {

    int deleteByPrimaryKey(Integer id);

    Long insert(RoutineUser row);

    RoutineUser selectByPrimaryKey(Integer id);

    int updateByPrimaryKey(RoutineUser row);

    RoutineUser selectByUserPhoneRoutineUser(Long phone);

    RoutineUser selectByUserPhone(Long phone);
}