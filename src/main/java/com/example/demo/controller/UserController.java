package com.example.demo.controller;

import com.example.demo.common.api.Result;
import com.example.demo.common.page.PageResult;
import com.example.demo.dto.UserCreateReq;
import com.example.demo.dto.UserPageReq;
import com.example.demo.dto.UserUpdateReq;
import com.example.demo.entity.User;
import com.example.demo.service.UserService;
import com.example.demo.vo.UserVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户管理示例接口
 */
@Tag(name = "用户管理", description = "用户增删改查与分页示例接口")
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @Operation(summary = "创建用户")
    @PostMapping
    public Result<Long> create(@Valid @RequestBody UserCreateReq req) {
        return Result.success(userService.create(req));
    }

    @Operation(summary = "更新用户")
    @PutMapping
    public Result<Void> update(@Valid @RequestBody UserUpdateReq req) {
        userService.update(req);
        return Result.success();
    }

    @Operation(summary = "根据ID删除用户（逻辑删除）")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        userService.removeById(id);
        return Result.success();
    }

    @Operation(summary = "根据ID查询用户（带Redis缓存示例）")
    @GetMapping("/{id}")
    public Result<UserVO> getById(@PathVariable Long id) {
        return Result.success(userService.getByIdWithCache(id));
    }

    @Operation(summary = "查询所有用户（仅示例）")
    @GetMapping
    public Result<List<User>> listAll() {
        return Result.success(userService.list());
    }

    @Operation(summary = "分页查询用户")
    @PostMapping("/page")
    public Result<PageResult<UserVO>> page(@Valid @RequestBody UserPageReq req) {
        return Result.success(userService.page(req));
    }
}

