package com.linknest.api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig {

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/api/**") // Apply to all API endpoints under /api
                        .allowedOrigins("http://localhost:5173") // Allow your React app's origin
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "HEAD", "OPTIONS") // Allow common methods
                        .allowedHeaders("*")
                        .allowCredentials(true);
            }
        };
    }
}