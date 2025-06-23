package com.example.my_estore_backend.config;

import com.example.my_estore_backend.repository.UserTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 定时任务配置类
 */
@Slf4j
@Configuration
@EnableScheduling
@RequiredArgsConstructor
public class SchedulingConfig {
    
    private final UserTokenRepository userTokenRepository;
    
    /**
     * 每天凌晨1点执行一次，清理过期的令牌
     */
    @Scheduled(cron = "0 0 1 * * ?")
    @Transactional
    public void cleanExpiredTokens() {
        try {
            log.info("开始清理过期的令牌...");
            userTokenRepository.deleteAllByExpiresAtBefore(LocalDateTime.now());
            log.info("过期令牌清理完成");
        } catch (Exception e) {
            log.error("清理过期令牌时发生错误", e);
        }
    }
} 