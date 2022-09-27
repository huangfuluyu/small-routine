package com.wechat.routine.common.req;

import lombok.Data;

/**
 * @author HuangFuLuYu
 * @date 2022/06/12/14:56
 * @Description
 */
@Data
public class PushQuisReq {

    private String page;

    private String scene;

    private String path;
}
