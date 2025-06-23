package com.example.my_estore_backend.model.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 用户信息更新请求DTO
 */
@Data
public class UserUpdateRequest {
    
    @Size(max = 100, message = "姓名长度不能超过100个字符")
    private String name;
    
    @Email(message = "邮箱格式不正确")
    private String email;
    
    private String bio;
} 