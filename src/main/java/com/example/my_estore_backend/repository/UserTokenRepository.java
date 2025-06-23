package com.example.my_estore_backend.repository;

import com.example.my_estore_backend.model.entity.User;
import com.example.my_estore_backend.model.entity.UserToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 用户令牌仓库接口
 */
@Repository
public interface UserTokenRepository extends JpaRepository<UserToken, Long> {
    
    /**
     * 根据令牌查找
     */
    Optional<UserToken> findByToken(String token);
    
    /**
     * 根据用户查找有效令牌
     */
    List<UserToken> findByUserAndExpiresAtAfter(User user, LocalDateTime now);
    
    /**
     * 删除用户的所有令牌
     */
    void deleteAllByUser(User user);
    
    /**
     * 删除过期令牌
     */
    void deleteAllByExpiresAtBefore(LocalDateTime now);
} 