package com.wechat.routine.mapper;

import com.wechat.routine.pojo.User;
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
public interface UserMapper {

    List<User> selectByRoleidUsers();

    User selectByUserNameAndPassword(String account,String password);

    User selectByAccount(String account);

    List<User> selectByDeptId(Integer deptId);

    List<User> selectAllByIdUsers(List<Integer> list);

    List<User> selectByAll();

    User selectById(Integer id);

}
