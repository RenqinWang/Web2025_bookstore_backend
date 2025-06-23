package com.example.my_estore_backend.service;

import com.example.my_estore_backend.model.dto.*;
import com.example.my_estore_backend.model.entity.User;
import org.springframework.web.multipart.MultipartFile;

/**
 * 用户服务接口
 */
public interface UserService {
    
    /**
     * 用户注册
     */
    UserResponse register(UserRegisterRequest request);
    
    /**
     * 用户登录
     */
    UserLoginResponse login(UserLoginRequest request);
    
    /**
     * 获取当前用户信息
     */
    UserResponse getCurrentUser(Long userId);
    
    /**
     * 获取用户信息
     */
    UserResponse getUserById(Long id);
    
    /**
     * 更新用户信息
     */
    UserResponse updateUser(Long id, UserUpdateRequest request);
    
    /**
     * 修改密码
     */
    void updatePassword(Long id, UserPasswordUpdateRequest request);
    
    /**
     * 上传头像
     */
    String uploadAvatar(Long id, MultipartFile avatar);
    
    /**
     * 用户登出
     */
    void logout(Long userId, String token);
    
    /**
     * 根据ID查找用户
     */
    User findById(Long id);
    
    /**
     * 根据用户名查找用户
     */
    User findByUsername(String username);
} 