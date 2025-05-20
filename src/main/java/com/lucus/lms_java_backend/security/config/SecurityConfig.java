/*
 * @Author : Linn Myat Maung
 * @Date   : 4/14/2025
 * @Time   : 3:27 PM
 */

package com.lucus.lms_java_backend.security.config;

import com.lucus.lms_java_backend.security.exception.CustomAuthenticationEntryPoint;
import com.lucus.lms_java_backend.security.utils.JwtUtil;
import lombok.RequiredArgsConstructor;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;

import java.util.Optional;
import java.util.function.Supplier;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomAuthenticationEntryPoint authenticationEntryPoint;

    private static final String ROLE_PREFIX = "ROLE_";
    private static final String ROLE_ADMIN = ROLE_PREFIX + "ADMIN";
    private static final String ROLE_USER = ROLE_PREFIX + "USER";

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .cors(Customizer.withDefaults())
                .authorizeHttpRequests(this::configureAuthorization)
                .csrf(AbstractHttpConfigurer::disable)
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(authenticationEntryPoint)
                )
                .build();
    }

    private void configureAuthorization(AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry auth) {
        // Public
        auth.requestMatchers(
                "/api/v1/auth/**",
                "/api/v1/public/**",
                "/swagger-ui.html",
                "/swagger-ui/**",
                "/v3/api-docs/**"
        ).permitAll();

        // Role-based Access
        auth.requestMatchers("/api/v1/users/**").access(hasRole(ROLE_ADMIN));
        auth.requestMatchers("/api/v1/admin/**").access(hasRole(ROLE_USER));

        // Shared access (Read: USER + ADMIN, Write: ADMIN only)
        applyUserReadAdminCRUD(auth, "/api/v1/category/**");
        applyUserReadAdminCRUD(auth, "/api/v1/course/**");
        applyUserReadAdminCRUD(auth, "/api/v1/lesson/**");

        auth.anyRequest().authenticated();
    }

    private void applyUserReadAdminCRUD(AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry auth, String basePath) {
        auth.requestMatchers(HttpMethod.GET, basePath).access(hasAnyRole(ROLE_USER, ROLE_ADMIN));
        auth.requestMatchers(HttpMethod.POST, basePath).access(hasRole(ROLE_ADMIN));
        auth.requestMatchers(HttpMethod.PUT, basePath).access(hasRole(ROLE_ADMIN));
        auth.requestMatchers(HttpMethod.DELETE, basePath).access(hasRole(ROLE_ADMIN));
    }

    private AuthorizationManager<RequestAuthorizationContext> hasRole(String requiredRole) {
        return (Supplier<Authentication> authSupplier, RequestAuthorizationContext context) -> {
            HttpServletRequest request = context.getRequest();
            String userRole = getRoleFromToken(request);
            return new AuthorizationDecision(userRole.equals(requiredRole));
        };
    }

    private AuthorizationManager<RequestAuthorizationContext> hasAnyRole(String... roles) {
        return (Supplier<Authentication> authSupplier, RequestAuthorizationContext context) -> {
            HttpServletRequest request = context.getRequest();
            String userRole = getRoleFromToken(request);
            for (String role : roles) {
                if (userRole.equals(role)) {
                    return new AuthorizationDecision(true);
                }
            }
            return new AuthorizationDecision(false);
        };
    }

    private String getRoleFromToken(HttpServletRequest request) {
        String token = Optional.ofNullable(request.getHeader("Authorization"))
                .filter(authHeader -> authHeader.startsWith("Bearer "))
                .map(authHeader -> authHeader.substring(7))
                .orElse(null);

        if (token == null || token.isEmpty()) {
            return "";
        }

        return Optional.ofNullable(JwtUtil.decodeToken(token))
                .map(claims -> claims.get("role", String.class))
                .orElse("");
    }
}
