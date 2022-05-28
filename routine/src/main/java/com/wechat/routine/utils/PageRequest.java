package com.wechat.routine.utils;

import lombok.Data;

/**
 * @author : HuangFu
 * @Description : PageRequest
 * @date : 2022-05-27 10:25
 **/
@Data
public class PageRequest {
    /**
     * 当前页码
     */
    private int pageNum;
    /**
     * 每页数量
     */
    private int pageSize;

}
