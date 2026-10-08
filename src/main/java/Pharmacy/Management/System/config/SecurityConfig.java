package Pharmacy.Management.System.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Authentication/authorisation for this module is handled manually (see AuthService,
 * AuthInterceptor) using HttpSession, because the frontend calls plain JSON endpoints
 * under /customer/** and /manager/** rather than Spring Security's form login.
 * The project only depends on spring-security-crypto (see pom.xml), so there is no
 * Spring Security filter chain on the classpath to configure or disable here — this
 * class just exposes the BCrypt password encoder used by AuthService and UserService.
 */
@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}