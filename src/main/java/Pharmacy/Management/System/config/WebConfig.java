package Pharmacy.Management.System.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * The frontend (static/index.html) is served by this same Spring Boot
 * app, so CORS is not required for normal use. This is kept only so the
 * API can still be called from a different origin during development
 * (e.g. testing with Postman, or opening index.html directly with
 * VS Code Live Server instead of through Spring Boot).
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOriginPatterns("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*");
    }
}
