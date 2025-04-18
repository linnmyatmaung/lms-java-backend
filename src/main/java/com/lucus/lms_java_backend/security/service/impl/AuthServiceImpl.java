/*
 * @Author : Linn Myat Maung
 * @Date   : 4/14/2025
 * @Time   : 4:07 PM
 */

package com.lucus.lms_java_backend.security.service.impl;


import com.lucus.lms_java_backend.api.role.model.Role;
import com.lucus.lms_java_backend.api.role.model.RoleName;
import com.lucus.lms_java_backend.api.role.repository.RoleRepository;
import com.lucus.lms_java_backend.api.token.dto.TokenDto;
import com.lucus.lms_java_backend.api.token.model.Token;
import com.lucus.lms_java_backend.api.token.repository.TokenRepository;
import com.lucus.lms_java_backend.api.user.dto.UserDto;
import com.lucus.lms_java_backend.api.user.model.User;
import com.lucus.lms_java_backend.api.user.repository.UserRepository;
import com.lucus.lms_java_backend.api.user.utils.UserUtil;
import com.lucus.lms_java_backend.config.response.dto.ApiResponse;
import com.lucus.lms_java_backend.config.utils.DtoUtil;
import com.lucus.lms_java_backend.security.dto.LoginRequest;
import com.lucus.lms_java_backend.security.dto.RegisterRequest;
import com.lucus.lms_java_backend.security.service.AuthService;
import com.lucus.lms_java_backend.security.service.JwtService;
import com.lucus.lms_java_backend.security.utils.ClaimsProvider;
import io.jsonwebtoken.Claims;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final TokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final UserUtil userUtil;
    private final ModelMapper modelMapper;

    @Override
    public ApiResponse authenticateUser(LoginRequest loginRequest) {
        log.info("Authenticating user with email: {}", loginRequest.getEmail());

        User user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> {
                    log.warn("User not found: {}", loginRequest.getEmail());
                    return new SecurityException("Invalid email or password");
                });

        Set<Role> roleList = user.getRoles();
        String roleName = roleList.stream()
                .map(role -> role.getName().name())
                .findFirst()
                .orElse("ROLE_USER");

        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            log.warn("Invalid password for user: {}", loginRequest.getEmail());
            return ApiResponse.builder()
                    .success(0)
                    .code(HttpStatus.UNAUTHORIZED.value())
                    .message("Invalid email or password")
                    .build();
        }

        log.info("User authenticated successfully: {}", loginRequest.getEmail());

        UserDto userDto = DtoUtil.map(user, UserDto.class, modelMapper);

        Token refreshToken = tokenRepository.findByUser(user)
                .orElseThrow(() -> {
                    log.warn("Token not found for user: {}", loginRequest.getEmail());
                    return new SecurityException("Token not found for user");
                });

        Map<String, Object> tokenData = generateTokens(user, roleName);

        TokenDto tokenDto = DtoUtil.map(refreshToken, TokenDto.class, modelMapper);

        return ApiResponse.builder()
                .success(1)
                .code(HttpStatus.OK.value())
                .data(Map.of(
                        "user", userDto,
                        "accessToken", tokenData.get("accessToken"),
                        "refreshToken", tokenDto.getRefreshtoken()))
                .message("You are successfully logged in!")
                .build();
    }

    @Override
    @Transactional
    public ApiResponse registerUser(RegisterRequest registerRequest) {
        log.info("Registering new user with email: {}", registerRequest.getEmail());

        if (userRepository.findByEmail(registerRequest.getEmail()).isPresent()) {
            log.warn("Email already exists: {}", registerRequest.getEmail());
            return ApiResponse.builder()
                    .success(0)
                    .code(HttpStatus.CONFLICT.value())
                    .message("Email is already in use")
                    .build();
        }

        Role userRole = roleRepository.findByName(RoleName.ROLE_USER)
                .orElseThrow(() -> new RuntimeException("Role not found in database!"));
        log.info("Assigning role: {}", userRole.getName());

        User newUser = User.builder()
                .name(registerRequest.getName())
                .username(userUtil.generateUniqueUsername(registerRequest.getName()))
                .email(registerRequest.getEmail())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .roles(Set.of(userRole))
                .build();

        userRepository.save(newUser);

        Map<String, Object> tokenData = generateTokens(newUser, String.valueOf(userRole.getName()));

        String accessToken = (String) tokenData.get("accessToken");
        String refreshToken = (String) tokenData.get("refreshToken");

        Instant expiredAt = Instant.now().plus(7, ChronoUnit.DAYS);

        Token token = Token.builder()
                .user(newUser)
                .refreshtoken(refreshToken)
                .expiredAt(expiredAt)
                .build();

        tokenRepository.save(token);

        log.info("User registered successfully: {}", registerRequest.getEmail());

        UserDto userDto = DtoUtil.map(newUser, UserDto.class, modelMapper);

        return ApiResponse.builder()
                .success(1)
                .code(HttpStatus.CREATED.value())
                .data(Map.of(
                        "user", userDto,
                        "accessToken", accessToken,
                        "refreshToken", refreshToken))
                .message("You have registered successfully.")
                .build();
    }

    private Map<String, Object> generateTokens(User user, String roleName) {
        log.debug("Generating tokens for user: {}", user.getEmail());

        String accessToken = jwtService.generateToken(ClaimsProvider.generateClaims(user), roleName,
                user.getEmail(), 15 * 60 * 1000);
        String refreshToken = jwtService.generateToken(ClaimsProvider.generateClaims(user), roleName,
                user.getEmail(), 7 * 24 * 60 * 60 * 1000);

        return Map.of("accessToken", accessToken, "refreshToken", refreshToken);
    }

    @Override
    public ApiResponse logout(String accessToken) {
        if (accessToken != null && accessToken.startsWith("Bearer ")) {
            String token = accessToken.substring(7);
            Claims claims = jwtService.validateToken(token);
            String userEmail = claims.getSubject();

            User user = userRepository.findByEmail(userEmail)
                    .orElseThrow(() -> new SecurityException(
                            "User not found. Cannot proceed with logout."));

            log.debug("Revoking access token for user: {}", user.getEmail());
            try {
                jwtService.revokeToken(token);
                log.info("User logged out successfully");
                return ApiResponse.builder()
                        .success(1)
                        .code(200)
                        .data(true)
                        .message("Logout successful")
                        .build();

            } catch (SecurityException ex) {
                log.warn("Logout failed due to security reasons: {}", ex.getMessage());
                throw ex;
            } catch (Exception ex) {
                log.error("Unexpected error during logout", ex);
                throw new RuntimeException("An error occurred during logout.");
            }
        }

        // Handle case when accessToken is null or does not start with "Bearer "
        return ApiResponse.builder()
                .success(0)
                .code(400)
                .data(false)
                .message("Invalid access token")
                .build();
    }

    @Override
    public ApiResponse refreshToken(String refreshToken) {
        log.info("Validating refresh token");

        Claims claims;
        try {
            claims = jwtService.validateToken(refreshToken);
        } catch (SecurityException ex) {
            log.warn("Invalid refresh token: {}", ex.getMessage());
            return ApiResponse.builder()
                    .success(0)
                    .code(HttpStatus.UNAUTHORIZED.value())
                    .data(ex.getMessage())
                    .message("Invalid or expired refresh token")
                    .build();
        }

        String email = claims.getSubject();
        User user = userRepository.findByEmail(email).orElse(null);

        if (user == null) {
            log.warn("User not found for refresh token: {}", email);
            return ApiResponse.builder()
                    .success(0)
                    .code(HttpStatus.UNAUTHORIZED.value())
                    .data(false)
                    .message("User not found")
                    .build();
        }

        log.info("Generating new access token for user: {}", email);

        Set<Role> roleList = user.getRoles();
        String roleName = roleList.stream()
                .map(role -> role.getName().name())
                .findFirst()
                .orElse("ROLE_USER");

        String newAccessToken = jwtService.generateToken(ClaimsProvider.generateClaims(user), roleName, email,
                15 * 60 * 1000);

        return ApiResponse.builder()
                .success(1)
                .code(HttpStatus.OK.value())
                .data(Map.of("accessToken", newAccessToken))
                .message("Access token refreshed successfully")
                .build();
    }

    @Override
    public ApiResponse getCurrentUser(String authHeader) {
        UserDto userDto = userUtil.getCurrentUserDto(authHeader);

        return ApiResponse.builder()
                .success(1)
                .code(HttpStatus.OK.value())
                .data(Map.of(
                        "user", userDto))
                .message("User retrieved successfully")
                .build();
    }

}
