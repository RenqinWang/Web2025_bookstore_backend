package com.example.my_estore_backend.service.impl;

import com.example.my_estore_backend.model.dto.*;
import com.example.my_estore_backend.model.entity.User;
import com.example.my_estore_backend.model.entity.UserToken;
import com.example.my_estore_backend.repository.UserRepository;
import com.example.my_estore_backend.repository.UserTokenRepository;
import com.example.my_estore_backend.security.JwtProvider;
import com.example.my_estore_backend.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 用户服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    
    private final UserRepository userRepository;
    private final UserTokenRepository userTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final AuthenticationManager authenticationManager;
    
    @Value("${file.avatar-dir}")
    private String avatarDir;
    
    @Override
    @Transactional
    public UserResponse register(UserRegisterRequest request) {
        // 检查用户名是否已存在
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalStateException("用户名已存在");
        }
        
        // 检查邮箱是否已存在（如果提供了邮箱）
        if (request.getEmail() != null && !request.getEmail().isEmpty() 
                && userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalStateException("邮箱已被使用");
        }
        
        // 创建新用户
        User user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .name(request.getName())
                .email(request.getEmail())
                .build();
        
        // 保存用户
        User savedUser = userRepository.save(user);
        
        return UserResponse.fromEntity(savedUser);
    }
    
    @Override
    @Transactional
    public UserLoginResponse login(UserLoginRequest request) {
        // 验证用户凭据
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );
        
        // 获取用户
        User user = findByUsername(request.getUsername());
        
        // 生成令牌
        String token;
        if (Boolean.TRUE.equals(request.getRemember())) {
            token = jwtProvider.generateLongLivedToken(user);
        } else {
            token = jwtProvider.generateToken(user);
        }
        
        // 保存令牌
        LocalDateTime expiresAt = LocalDateTime.now().plusDays(
                Boolean.TRUE.equals(request.getRemember()) ? 30 : 1
        );
        
        UserToken userToken = UserToken.builder()
                .user(user)
                .token(token)
                .expiresAt(expiresAt)
                .build();
        
        userTokenRepository.save(userToken);
        
        // 返回登录响应
        return UserLoginResponse.builder()
                .token(token)
                .user(UserResponse.fromEntity(user))
                .build();
    }
    
    @Override
    public UserResponse getCurrentUser(Long userId) {
        User user = findById(userId);
        return UserResponse.fromEntity(user);
    }
    
    @Override
    public UserResponse getUserById(Long id) {
        User user = findById(id);
        return UserResponse.fromEntity(user);
    }
    
    @Override
    @Transactional
    public UserResponse updateUser(Long id, UserUpdateRequest request) {
        User user = findById(id);
        
        // 更新用户信息
        if (request.getName() != null) {
            user.setName(request.getName());
        }
        
        if (request.getEmail() != null && !request.getEmail().equals(user.getEmail())) {
            // 检查邮箱是否已被使用
            if (userRepository.existsByEmail(request.getEmail())) {
                throw new IllegalStateException("邮箱已被使用");
            }
            user.setEmail(request.getEmail());
        }
        
        if (request.getBio() != null) {
            user.setBio(request.getBio());
        }
        
        // 保存更新
        User updatedUser = userRepository.save(user);
        
        return UserResponse.fromEntity(updatedUser);
    }
    
    @Override
    @Transactional
    public void updatePassword(Long id, UserPasswordUpdateRequest request) {
        User user = findById(id);
        
        // 验证旧密码
        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new IllegalStateException("旧密码不正确");
        }
        
        // 更新密码
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
        
        // 可选：撤销所有现有令牌
        userTokenRepository.deleteAllByUser(user);
    }
    
    @Override
    @Transactional
    public String uploadAvatar(Long id, MultipartFile avatar) {
        try {
            User user = findById(id);
            
            // 确保目录存在
            Path avatarPath = Paths.get(avatarDir);
            if (!Files.exists(avatarPath)) {
                Files.createDirectories(avatarPath);
            }
            
            // 生成唯一文件名
            String extension = FilenameUtils.getExtension(avatar.getOriginalFilename());
            String fileName = id + "_" + UUID.randomUUID() + "." + extension;
            Path targetPath = avatarPath.resolve(fileName);
            
            // 保存文件
            Files.copy(avatar.getInputStream(), targetPath);
            
            // 更新用户头像URL
            String avatarUrl = "/static/avatars/" + fileName;
            user.setAvatar(avatarUrl);
            userRepository.save(user);
            
            return avatarUrl;
        } catch (IOException e) {
            log.error("上传头像失败", e);
            throw new RuntimeException("上传头像失败: " + e.getMessage());
        }
    }
    
    @Override
    @Transactional
    public void logout(Long userId, String token) {
        // 查找并删除令牌
        userTokenRepository.findByToken(token)
                .ifPresent(userTokenRepository::delete);
    }
    
    @Override
    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("用户不存在，ID: " + id));
    }
    
    @Override
    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("用户不存在，用户名: " + username));
    }
} 