/*
 * @Author : Linn Myat Maung
 * @Date   : 4/8/2025
 * @Time   : 5:08 PM
 */

package com.lucus.lms_java_backend.config.beans;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
public class CorsConfig {

    @Value("${application.frontend-urls}")
    private String frontendUrlsCsv;

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        List<String> allowedFrontendUrls = parseFrontendUrls(frontendUrlsCsv);
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(allowedFrontendUrls);
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    private List<String> parseFrontendUrls(String csv) {
        return List.of(csv.split("\\s*,\\s*"));
    }
}