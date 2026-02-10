package com.example.demo.common.page;

import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;

/**
 * 通用分页结果封装
 */
@Data
public class PageResult<T> implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long pageNo;
    private Long pageSize;
    private Long total;
    private List<T> records;

    public static <T> PageResult<T> empty(PageQuery query) {
        PageResult<T> r = new PageResult<>();
        r.setPageNo(query.getPageNo());
        r.setPageSize(query.getPageSize());
        r.setTotal(0L);
        r.setRecords(Collections.emptyList());
        return r;
    }

    public static <T> PageResult<T> from(IPage<T> page) {
        PageResult<T> r = new PageResult<>();
        r.setPageNo(page.getCurrent());
        r.setPageSize(page.getSize());
        r.setTotal(page.getTotal());
        r.setRecords(page.getRecords());
        return r;
    }
}

