package com.wechat.routine.mapper;

import com.wechat.routine.pojo.RoutineScore;
import com.wechat.routine.pojo.User;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

/**
 * @author : HuangFu
 * @Description : UserMapper
 * @date : 2022-05-27 15:59
 **/

@Mapper
@Component
public interface RoutineScoreMapper {

    /**
     * 插入满意度
     * @param score
     * @return
     */
    int insertRoutineScore(RoutineScore score);

}
