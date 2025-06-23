package com.example.my_estore_backend.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * 用户实体类
 */
@Entity
@Table(name = "users")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true, length = 50)
    private String username;
    
    @Column(nullable = false)
    private String password;
    
    @Column(nullable = false, length = 100)
    private String name;
    
    @Column
    private String avatar;
    
    @Column(nullable = false, length = 20)
    private String role;
    
    @Column(unique = true, length = 100)
    private String email;
    
    @Column(columnDefinition = "TEXT")
    private String bio;
    
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;
    
    // 预设默认值
    @PrePersist
    public void prePersist() {
        if (this.role == null) {
            this.role = "user";
        }
        if (this.avatar == null) {
            this.avatar = "https://picsum.photos/id/42/300/400";
        }
    }
} 