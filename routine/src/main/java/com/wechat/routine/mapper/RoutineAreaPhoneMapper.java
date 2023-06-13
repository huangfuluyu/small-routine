package com.wechat.routine.mapper;

import com.wechat.routine.pojo.RoutineAreaPhone;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author HuangFuLuYu
 * @date 2022/10/18/18:35
 * @Description
 */
@Mapper
@Component
public interface RoutineAreaPhoneMapper {
    List<RoutineAreaPhone> selectAll();
}
