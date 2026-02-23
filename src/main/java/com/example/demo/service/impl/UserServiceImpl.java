package com.example.demo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.demo.common.exception.BusinessException;
import com.example.demo.common.page.PageResult;
import com.example.demo.common.service.impl.BaseServiceImpl;
import com.example.demo.common.util.RedisUtil;
import com.example.demo.dto.UserCreateReq;
import com.example.demo.dto.UserPageReq;
import com.example.demo.dto.UserUpdateReq;
import com.example.demo.entity.User;
import com.example.demo.mapper.UserMapper;
import com.example.demo.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import com.example.demo.vo.UserVO;

/**
 * 用户服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl extends BaseServiceImpl<UserMapper, User> implements UserService {

    private static final String USER_CACHE_KEY_PREFIX = "user::";
    private final RedisUtil redisUtil;

    @Override
    public Long create(UserCreateReq req) {
        User user = new User();
        user.setUsername(req.getUsername());
        user.setNickname(req.getNickname());
        user.setEmail(req.getEmail());
        user.setMobile(req.getMobile());
        this.save(user);
        log.info("创建用户成功, id={}", user.getId());
        return user.getId();
    }

    @Override
    public void update(UserUpdateReq req) {
        User user = this.getById(req.getId());
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        if (Objects.nonNull(req.getNickname())) {
            user.setNickname(req.getNickname());
        }
        if (Objects.nonNull(req.getEmail())) {
            user.setEmail(req.getEmail());
        }
        if (Objects.nonNull(req.getMobile())) {
            user.setMobile(req.getMobile());
        }
        this.updateById(user);
        // 清理缓存
        String cacheKey = buildUserCacheKey(user.getId());
        redisUtil.delete(cacheKey);
        log.info("更新用户成功并清理缓存, id={}", user.getId());
    }

    @Override
    public UserVO getByIdWithCache(Long id) {
        String cacheKey = buildUserCacheKey(id);
        UserVO cached = redisUtil.get(cacheKey);
        if (cached != null) {
            log.info("从缓存中读取用户, id={}", id);
            return cached;
        }
        User user = this.getById(id);
        if (user == null) {
            return null;
        }
        UserVO vo = toVO(user);
        redisUtil.set(cacheKey, vo, Duration.ofMinutes(5));
        log.info("写入用户缓存, id={}", id);
        return vo;
    }

    @Override
    public PageResult<UserVO> page(UserPageReq req) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.isNotBlank(req.getUsername())) {
            wrapper.like(User::getUsername, req.getUsername());
        }
        if (StringUtils.isNotBlank(req.getNickname())) {
            wrapper.like(User::getNickname, req.getNickname());
        }
        Page<User> page = this.page(req.toMpPage(), wrapper);
        PageResult<UserVO> result = new PageResult<>();
        result.setPageNo(page.getCurrent());
        result.setPageSize(page.getSize());
        result.setTotal(page.getTotal());
        result.setRecords(toVOList(page.getRecords()));
        return result;
    }

    private String buildUserCacheKey(Long id) {
        return USER_CACHE_KEY_PREFIX + id;
    }

    private UserVO toVO(User user) {
        if (user == null) {
            return null;
        }
        UserVO vo = new UserVO();
        vo.setId(user.getId());
        vo.setUsername(user.getUsername());
        vo.setNickname(user.getNickname());
        vo.setEmail(user.getEmail());
        vo.setMobile(user.getMobile());
        return vo;
    }

    private List<UserVO> toVOList(List<User> list) {
        if (list == null) {
            return List.of();
        }
        return list.stream()
                .map(this::toVO)
                .collect(Collectors.toList());
    }
}

