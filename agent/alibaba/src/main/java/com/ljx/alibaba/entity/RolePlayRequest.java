package com.ljx.alibaba.entity;

import lombok.Data;

/**
 * 角色扮演请求
 */
@Data
public class RolePlayRequest {
    private String message;
    private String role; // teacher, doctor, programmer, translator
}
