package com.example.my_estore_backend.model.dto;

import com.example.my_estore_backend.model.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户信息响应DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    
    private Long id;
    private String username;
    private String name;
    private String avatar;
    private String role;
    private String email;
    private String bio;
    
    /**
     * 从用户实体转换为用户响应DTO
     */
    public static UserResponse fromEntity(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .name(user.getName())
                .avatar(user.getAvatar())
                .role(user.getRole())
                .email(user.getEmail())
                .bio(user.getBio())
                .build();
    }
} 