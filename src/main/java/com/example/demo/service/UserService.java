package com.example.demo.service;

import com.example.demo.common.page.PageResult;
import com.example.demo.common.service.BaseService;
import com.example.demo.dto.UserCreateReq;
import com.example.demo.dto.UserPageReq;
import com.example.demo.dto.UserUpdateReq;
import com.example.demo.entity.User;
import com.example.demo.vo.UserVO;

/**
 * 用户服务
 */
public interface UserService extends BaseService<User> {

    /**
     * 创建用户
     */
    Long create(UserCreateReq req);

    /**
     * 更新用户
     */
    void update(UserUpdateReq req);

    /**
     * 根据 ID 查询用户（带缓存示例）
     */
    UserVO getByIdWithCache(Long id);

    /**
     * 分页查询
     */
    PageResult<UserVO> page(UserPageReq req);
}

