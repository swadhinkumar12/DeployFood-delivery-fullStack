package com.fooddelivery;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * Main entry point for Food Delivery Application
 * Spring Boot auto-configures everything from the classpath
 */
@SpringBootApplication
@EntityScan("com.fooddelivery.entity")
@EnableJpaRepositories("com.fooddelivery.repository")
@EnableAsync
public class FoodDeliveryApplication {
    public static void main(String[] args) {
        SpringApplication.run(FoodDeliveryApplication.class, args);
    }
}
