package com.example.my_estore_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * 电子书城后端应用主类
 */
@SpringBootApplication
@EnableScheduling
@EnableTransactionManagement
public class MyEstoreBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(MyEstoreBackendApplication.class, args);
    }

}
