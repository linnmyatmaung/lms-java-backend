/*
 * @Author : Linn Myat Maung
 * @Date   : 4/14/2025
 * @Time   : 8:51 AM
 */

package com.lucus.lms_java_backend.api.user.utils;

import com.lucus.lms_java_backend.api.user.dto.UserDto;
import com.lucus.lms_java_backend.api.user.model.User;
import com.lucus.lms_java_backend.api.user.repository.UserRepository;
import com.lucus.lms_java_backend.config.utils.DtoUtil;
import com.lucus.lms_java_backend.security.service.JwtService;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;

import org.springframework.stereotype.Component;

import java.text.Normalizer;
import java.util.Locale;

@Component
@Slf4j
public class UserUtil {

    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    public UserUtil(JwtService jwtService, UserRepository userRepository, ModelMapper modelMapper) {
        this.jwtService = jwtService;
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
    }

    public UserDto getCurrentUserDto(String authHeader) {
        String email = extractEmailFromToken(authHeader);
        User user = findUserByEmail(email);
        return DtoUtil.map(user, UserDto.class, modelMapper);
    }

    public String extractEmailFromToken(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.warn("Invalid Authorization header received");
            throw new SecurityException("Unauthorized: Missing or invalid token");
        }

        String token = authHeader.substring(7);
        Claims claims = jwtService.validateToken(token);
        return claims.getSubject();
    }

    public User findUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.warn("User not found for email: {}", email);
                    return new SecurityException("User not found");
                });
    }

    public String generateUniqueUsername(String fullName) {
        if (fullName == null || fullName.trim().isEmpty()) {
            throw new IllegalArgumentException("Full name cannot be empty.");
        }

        String baseUsername = normalizeUsername(fullName);
        String generatedUsername = baseUsername;

        int count = 1;
        while (userRepository.existsByUsername(generatedUsername)) {
            generatedUsername = baseUsername + count;
            count++;
        }

        return generatedUsername;
    }

    private String normalizeUsername(String fullName) {
        String normalized = Normalizer.normalize(fullName, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toLowerCase(Locale.ENGLISH)
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("\\.+", "-")
                .replaceAll("^\\.|\\.$", "");

        return normalized.length() > 20 ? normalized.substring(0, 20) : normalized;
    }
}
