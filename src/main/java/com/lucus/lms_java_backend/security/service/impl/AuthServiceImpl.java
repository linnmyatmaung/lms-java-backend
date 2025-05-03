/*
 * @Author : Linn Myat Maung
 * @Date   : 4/14/2025
 * @Time   : 4:07 PM
 */

package com.lucus.lms_java_backend.security.service.impl;

import com.lucus.lms_java_backend.api.role.model.Role;
import com.lucus.lms_java_backend.api.role.model.RoleName;
import com.lucus.lms_java_backend.api.role.repository.RoleRepository;
import com.lucus.lms_java_backend.api.token.model.Token;
import com.lucus.lms_java_backend.api.token.repository.TokenRepository;
import com.lucus.lms_java_backend.api.user.dto.UserDto;
import com.lucus.lms_java_backend.api.user.model.User;
import com.lucus.lms_java_backend.api.user.repository.UserRepository;
import com.lucus.lms_java_backend.api.user.utils.PasswordValidatorUtil;
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

    private static final long ACCESS_TOKEN_VALIDITY_MS = 15 * 60 * 1000; // 15 minutes
    private static final long REFRESH_TOKEN_VALIDITY_MS = 7 * 24 * 60 * 60 * 1000; // 7 days

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final TokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final UserUtil userUtil;
    private final ModelMapper modelMapper;

    @Override
    @Transactional
    public ApiResponse authenticateUser(LoginRequest loginRequest) {
        User user = findUserByEmail(loginRequest.getEmail());

        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            return buildErrorResponse("Invalid email or password", HttpStatus.UNAUTHORIZED);
        }

        String roleName = getUserRoleName(user);
        Map<String, Object> tokens = generateTokens(user, roleName);

        Token refreshToken = tokenRepository.findByUser(user).orElse(Token.builder().user(user).build());
        refreshToken.setRefreshtoken((String) tokens.get("refreshToken"));
        refreshToken.setExpiredAt(Instant.now().plus(7, ChronoUnit.DAYS));
        tokenRepository.save(refreshToken);

        UserDto userDto = DtoUtil.map(user, UserDto.class, modelMapper);

        log.info("User {} logged in successfully", user.getEmail());
        return ApiResponse.builder()
                .success(1)
                .code(HttpStatus.OK.value())
                .data(Map.of(
                        "user", userDto,
                        "accessToken", tokens.get("accessToken"),
                        "refreshToken", tokens.get("refreshToken")
                ))
                .message("Logged in successfully")
                .build();
    }

    @Override
    @Transactional
    public ApiResponse registerUser(RegisterRequest registerRequest) {
        if (userRepository.findByEmail(registerRequest.getEmail()).isPresent()) {
            return buildErrorResponse("Email already in use", HttpStatus.CONFLICT);
        }

        if (!PasswordValidatorUtil.isValid(registerRequest.getPassword())) {
            return buildErrorResponse("Password must include uppercase, lowercase, number, and special character", HttpStatus.BAD_REQUEST);
        }

        Role userRole = roleRepository.findByName(RoleName.ROLE_USER)
                .orElseThrow(() -> new RuntimeException("Role not found"));

        User newUser = User.builder()
                .name(registerRequest.getName())
                .username(userUtil.generateUniqueUsername(registerRequest.getName()))
                .email(registerRequest.getEmail())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .roles(Set.of(userRole))
                .build();

        userRepository.save(newUser);

        Map<String, Object> tokens = generateTokens(newUser, userRole.getName().name());

        Token token = Token.builder()
                .user(newUser)
                .refreshtoken((String) tokens.get("refreshToken"))
                .expiredAt(Instant.now().plus(7, ChronoUnit.DAYS))
                .build();
        tokenRepository.save(token);

        UserDto userDto = DtoUtil.map(newUser, UserDto.class, modelMapper);

        log.info("User {} registered successfully", newUser.getEmail());
        return ApiResponse.builder()
                .success(1)
                .code(HttpStatus.CREATED.value())
                .data(Map.of(
                        "user", userDto,
                        "accessToken", tokens.get("accessToken"),
                        "refreshToken", tokens.get("refreshToken")
                ))
                .message("Registration successful")
                .build();
    }

    @Override
    @Transactional
    public ApiResponse logout(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return buildErrorResponse("Invalid access token", HttpStatus.BAD_REQUEST);
        }

        String accessToken = authHeader.substring(7);
        Claims claims = jwtService.validateToken(accessToken);
        String email = claims.getSubject();

        User user = findUserByEmail(email);

        jwtService.revokeToken(accessToken);

        log.info("User {} logged out", email);
        return ApiResponse.builder()
                .success(1)
                .code(HttpStatus.OK.value())
                .data(true)
                .message("Logout successful")
                .build();
    }

    @Override
    @Transactional
    public ApiResponse refreshToken(String refreshToken) {
        try {
            Claims claims = jwtService.validateToken(refreshToken);
            String email = claims.getSubject();

            User user = findUserByEmail(email);

            Token storedToken = tokenRepository.findByUser(user)
                    .orElseThrow(() -> new SecurityException("Refresh token not found"));

            if (!storedToken.getRefreshtoken().equals(refreshToken)) {
                return buildErrorResponse("Invalid refresh token", HttpStatus.UNAUTHORIZED);
            }

            String roleName = getUserRoleName(user);

            String newAccessToken = jwtService.generateToken(
                    ClaimsProvider.generateClaims(user),
                    roleName,
                    email,
                    ACCESS_TOKEN_VALIDITY_MS
            );

            log.info("Refreshed access token for {}", email);
            return ApiResponse.builder()
                    .success(1)
                    .code(HttpStatus.OK.value())
                    .data(Map.of("accessToken", newAccessToken))
                    .message("Access token refreshed successfully")
                    .build();

        } catch (SecurityException ex) {
            log.error("Refresh token validation failed: {}", ex.getMessage());
            return buildErrorResponse("Invalid or expired refresh token", HttpStatus.UNAUTHORIZED);
        }
    }

    @Override
    @Transactional
    public ApiResponse getCurrentUser(String authHeader) {
        UserDto userDto = userUtil.getCurrentUserDto(authHeader);

        return ApiResponse.builder()
                .success(1)
                .code(HttpStatus.OK.value())
                .data(Map.of("user", userDto))
                .message("User retrieved successfully")
                .build();
    }

    private Map<String, Object> generateTokens(User user, String roleName) {
        String accessToken = jwtService.generateToken(
                ClaimsProvider.generateClaims(user),
                roleName,
                user.getEmail(),
                ACCESS_TOKEN_VALIDITY_MS
        );
        String refreshToken = jwtService.generateToken(
                ClaimsProvider.generateClaims(user),
                roleName,
                user.getEmail(),
                REFRESH_TOKEN_VALIDITY_MS
        );
        return Map.of("accessToken", accessToken, "refreshToken", refreshToken);
    }

    private User findUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new SecurityException("User not found"));
    }

    private String getUserRoleName(User user) {
        return user.getRoles().stream()
                .map(role -> role.getName().name())
                .findFirst()
                .orElse(RoleName.ROLE_USER.name());
    }

    private ApiResponse buildErrorResponse(String message, HttpStatus status) {
        return ApiResponse.builder()
                .success(0)
                .code(status.value())
                .message(message)
                .build();
    }
}
