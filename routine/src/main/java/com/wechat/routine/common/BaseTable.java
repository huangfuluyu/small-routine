package com.wechat.routine.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BaseTable implements Serializable {

    public Long id;
    public Date createTime;
    public Date modifyTime;
}
