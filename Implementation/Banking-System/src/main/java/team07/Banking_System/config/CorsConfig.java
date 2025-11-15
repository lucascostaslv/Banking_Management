package team07.Banking_System.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins(
                    "http://localhost:3000", 
                    "http://127.0.0.1:3000", 
                    "http://localhost:8080",
                    "http://127.0.0.1:8080",
                    "http://localhost:5500",  // Live Server (VS Code)
                    "http://127.0.0.1:5500",
                    "http://localhost:8000",   // Python HTTP Server
                    "http://127.0.0.1:8000"
                )
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600); // Cache preflight por 1 hora
    }
}