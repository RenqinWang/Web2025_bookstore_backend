package com.example.my_estore_backend.controller;

import com.example.my_estore_backend.common.ApiResponse;
import com.example.my_estore_backend.model.dto.UserPasswordUpdateRequest;
import com.example.my_estore_backend.model.dto.UserResponse;
import com.example.my_estore_backend.model.dto.UserUpdateRequest;
import com.example.my_estore_backend.security.JwtProvider;
import com.example.my_estore_backend.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

/**
 * 用户控制器
 */
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    
    private final UserService userService;
    private final JwtProvider jwtProvider;
    
    /**
     * 获取当前用户信息
     */
    @GetMapping("/current")
    public ResponseEntity<ApiResponse<UserResponse>> getCurrentUser(HttpServletRequest request) {
        String token = request.getHeader("Authorization").substring(7);
        Long userId = jwtProvider.getUserIdFromToken(token);
        UserResponse user = userService.getCurrentUser(userId);
        return ResponseEntity.ok(ApiResponse.success("获取成功", user));
    }
    
    /**
     * 获取指定用户信息
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> getUserById(@PathVariable Long id) {
        UserResponse user = userService.getUserById(id);
        return ResponseEntity.ok(ApiResponse.success(user));
    }
    
    /**
     * 更新用户信息
     */
    @PatchMapping("/{id}")
    @PreAuthorize("authentication.principal.username == @userService.findById(#id).username")
    public ResponseEntity<ApiResponse<UserResponse>> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UserUpdateRequest request) {
        UserResponse updatedUser = userService.updateUser(id, request);
        return ResponseEntity.ok(ApiResponse.success("更新成功", updatedUser));
    }
    
    /**
     * 更新用户密码
     */
    @PatchMapping("/{id}/password")
    @PreAuthorize("authentication.principal.username == @userService.findById(#id).username")
    public ResponseEntity<ApiResponse<Void>> updatePassword(
            @PathVariable Long id,
            @Valid @RequestBody UserPasswordUpdateRequest request) {
        userService.updatePassword(id, request);
        return ResponseEntity.ok(ApiResponse.success("密码修改成功", null));
    }
    
    /**
     * 上传用户头像
     */
    @PostMapping(value = "/{id}/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("authentication.principal.username == @userService.findById(#id).username")
    public ResponseEntity<ApiResponse<Map<String, String>>> uploadAvatar(
            @PathVariable Long id,
            @RequestParam("avatar") MultipartFile avatar) {
        
        String avatarUrl = userService.uploadAvatar(id, avatar);
        
        Map<String, String> result = new HashMap<>();
        result.put("avatarUrl", avatarUrl);
        
        return ResponseEntity.ok(ApiResponse.success("上传成功", result));
    }
} 