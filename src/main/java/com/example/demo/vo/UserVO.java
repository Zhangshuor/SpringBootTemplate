package com.example.demo.vo;

import lombok.Data;

/**
 * 用户返回 VO（对外展示用）
 */
@Data
public class UserVO {

    private Long id;
    private String username;
    private String nickname;
    private String email;
    private String mobile;
}

