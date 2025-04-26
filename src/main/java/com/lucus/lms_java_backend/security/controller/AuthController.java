/*
 * @Author : Linn Myat Maung
 * @Date   : 4/14/2025
 * @Time   : 4:22 PM
 */

package com.lucus.lms_java_backend.security.controller;

import com.lucus.lms_java_backend.config.response.dto.ApiResponse;
import com.lucus.lms_java_backend.config.response.utils.ResponseUtil;
import com.lucus.lms_java_backend.security.dto.LoginRequest;
import com.lucus.lms_java_backend.security.dto.RefreshTokenData;
import com.lucus.lms_java_backend.security.dto.RegisterRequest;
import com.lucus.lms_java_backend.security.service.AuthService;
import com.lucus.lms_java_backend.security.service.JwtService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/${api.base.path}/auth")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Authentication", description = "Authentication related endpoints")
public class AuthController {

    private final AuthService authService;
    public final JwtService jwtService;

    @PostMapping("/login")
    @Operation(summary = "Login user", description = "Authenticate user with email and password")
    public ResponseEntity<ApiResponse> login(@RequestBody LoginRequest loginRequest, HttpServletRequest request) {
        log.info("Received login attempt for email: {}", loginRequest.getEmail());
        ApiResponse response = authService.authenticateUser(loginRequest);
        log.info(response.getSuccess() == 1 ? "Login successful for user: {}" : "Login failed for user: {}", loginRequest.getEmail());
        return ResponseUtil.buildResponse(request, response, 0L);
    }

    @PostMapping("/logout")
    @Operation(summary = "Logout user", description = "Invalidate the current user's session/token")
    public ResponseEntity<ApiResponse> logout(
            @RequestHeader(value = "Authorization", required = false) String accessToken,
            HttpServletRequest request) {
        log.info("Received logout request");
        ApiResponse response = authService.logout(accessToken);
        log.info(response.getSuccess() == 1 ? "User logout successfully" : "Logout failed for security reason");
        return ResponseUtil.buildResponse(request, response, 0L);
    }

    @PostMapping("/register")
    @Operation(summary = "Register new user", description = "Create a new user account")
    public ResponseEntity<ApiResponse> register(@Validated @RequestBody RegisterRequest registerRequest,
                                                HttpServletRequest request) {
        log.info("Received registration request for email: {}", registerRequest.getEmail());
        ApiResponse response = authService.registerUser(registerRequest);
        log.info(response.getSuccess() == 1 ? "User registered successfully: {}" : "Registration failed for email: {}", registerRequest.getEmail());
        return ResponseUtil.buildResponse(request, response, 0L);
    }

    @PostMapping("/refresh")
    @Operation(summary = "Refresh access token", description = "Refresh JWT token using a valid refresh token")
    public ResponseEntity<ApiResponse> refresh(@Validated @RequestBody RefreshTokenData refreshTokenData,
                                               HttpServletRequest request) {
        log.info("Received token refresh request");
        ApiResponse response = authService.refreshToken(refreshTokenData.getRefreshToken());
        log.info(response.getSuccess() == 1 ? "Token refreshed successfully" : "Token refresh failed");
        return ResponseUtil.buildResponse(request, response, 0L);
    }

    @GetMapping("/me")
    @Operation(summary = "Get current user", description = "Retrieve information of the authenticated user")
    public ResponseEntity<ApiResponse> getCurrentUser(@RequestHeader("Authorization") String authHeader,
                                                      HttpServletRequest request) {
        log.info("Fetching current authenticated user");
        double requestStartTime = System.currentTimeMillis();
        ApiResponse response = authService.getCurrentUser(authHeader);
        return ResponseUtil.buildResponse(request, response, requestStartTime);
    }
}