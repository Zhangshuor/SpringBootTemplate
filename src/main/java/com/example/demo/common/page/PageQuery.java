package com.example.demo.common.page;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 通用分页查询请求
 */
@Data
public class PageQuery {

    @NotNull
    @Min(value = 1, message = "当前页必须大于等于1")
    private Long pageNo = 1L;

    @NotNull
    @Min(value = 1, message = "页大小必须大于等于1")
    @Max(value = 1000, message = "页大小不能超过1000")
    private Long pageSize = 10L;

    public <T> Page<T> toMpPage() {
        return Page.of(pageNo, pageSize);
    }
}

