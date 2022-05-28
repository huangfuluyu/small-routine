package com.wechat.routine.utils;

import lombok.Data;

import java.util.List;

/**
 * @author : HuangFu
 * @Description : PageResult
 * @date : 2022-05-27 10:26
 **/
@Data
public class PageResult {
    /**
     * 当前页码
     */
    private int pageNum;
    /**
     * 每页数量
     */
    private int pageSize;
    /**
     * 记录总数
     */
    private long totalSize;
    /**
     * 页码总数
     */
    private int totalPages;
    /**
     * 数据模型
     */
    private List<?> content;

}
