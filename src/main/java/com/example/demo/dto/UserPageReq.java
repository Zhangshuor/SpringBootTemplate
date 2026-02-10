package com.example.demo.dto;

import com.example.demo.common.page.PageQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 用户分页查询请求
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class UserPageReq extends PageQuery {

    /**
     * 用户名（模糊匹配）
     */
    private String username;

    /**
     * 昵称（模糊匹配）
     */
    private String nickname;
}

