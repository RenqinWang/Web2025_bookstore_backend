package com.example.my_estore_backend.controller;

import com.example.my_estore_backend.common.ApiResponse;
import com.example.my_estore_backend.model.dto.UserLoginRequest;
import com.example.my_estore_backend.model.dto.UserLoginResponse;
import com.example.my_estore_backend.model.dto.UserRegisterRequest;
import com.example.my_estore_backend.model.dto.UserResponse;
import com.example.my_estore_backend.security.JwtProvider;
import com.example.my_estore_backend.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 认证控制器
 */
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    
    private final UserService userService;
    private final JwtProvider jwtProvider;
    
    @Value("${jwt.header}")
    private String jwtHeader;
    
    @Value("${jwt.token-prefix}")
    private String jwtTokenPrefix;
    
    /**
     * 用户注册
     */
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserResponse>> register(@Valid @RequestBody UserRegisterRequest request) {
        UserResponse response = userService.register(request);
        return ResponseEntity.ok(ApiResponse.success("注册成功", response));
    }
    
    /**
     * 用户登录
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<UserLoginResponse>> login(@Valid @RequestBody UserLoginRequest request) {
        UserLoginResponse response = userService.login(request);
        return ResponseEntity.ok(ApiResponse.success("登录成功", response));
    }
    
    /**
     * 用户登出
     */
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(HttpServletRequest request) {
        String token = getJwtFromRequest(request);
        
        if (StringUtils.hasText(token)) {
            Long userId = jwtProvider.getUserIdFromToken(token);
            userService.logout(userId, token);
        }
        
        return ResponseEntity.ok(ApiResponse.success("登出成功", null));
    }
    
    /**
     * 从请求中提取JWT令牌
     */
    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader(jwtHeader);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(jwtTokenPrefix + " ")) {
            return bearerToken.substring(jwtTokenPrefix.length() + 1);
        }
        return null;
    }
} 