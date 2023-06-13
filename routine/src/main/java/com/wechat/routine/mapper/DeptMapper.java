package com.wechat.routine.mapper;

import com.wechat.routine.pojo.Dept;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author : HuangFu
 * @Description : UserMapper
 * @date : 2022-05-27 15:59
 **/

@Mapper
@Component
public interface DeptMapper {
    List<Dept> listByIdDepts(Integer id);

    List<Dept> listByFullNameDept(String fullName);
}
