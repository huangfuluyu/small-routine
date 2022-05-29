package com.wechat.routine.enums;

import lombok.Data;

/**
 * @author HuangFuLuYu
 * @date 2022/05/29/14:12
 * @Description
 */

public enum UserTypeEnum {
    USER(1,"用户"),
    MAINTAINER(2,"维护人员"),
    ADMIN(3,"管理员");
    private int num;
    private String type;

    public int getNum() {
        return num;
    }

    public String getType() {
        return type;
    }
    UserTypeEnum(int num, String type) {
        this.num = num;
        this.type = type;
    }
}
