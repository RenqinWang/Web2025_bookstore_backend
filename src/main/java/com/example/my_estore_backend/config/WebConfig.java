package com.example.my_estore_backend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Web配置类
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {
    
    @Value("${file.upload-dir}")
    private String uploadDir;
    
    @Value("${file.avatar-dir}")
    private String avatarDir;
    
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 配置上传文件的访问路径
        Path uploadPath = Paths.get(uploadDir);
        String uploadAbsolutePath = uploadPath.toFile().getAbsolutePath();
        
        registry.addResourceHandler("/static/**")
                .addResourceLocations("file:" + uploadAbsolutePath + "/");
        
        // 配置头像文件的访问路径
        Path avatarPath = Paths.get(avatarDir);
        String avatarAbsolutePath = avatarPath.toFile().getAbsolutePath();
        
        registry.addResourceHandler("/static/avatars/**")
                .addResourceLocations("file:" + avatarAbsolutePath + "/");
    }
} 