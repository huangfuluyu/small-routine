package com.wechat.routine.common.req;

import lombok.Data;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author HuangFuLuYu
 * @date 2022/06/06/20:26
 * @Description
 */

@Data
public class LoginReq {
    String userName;
    String password;
}
