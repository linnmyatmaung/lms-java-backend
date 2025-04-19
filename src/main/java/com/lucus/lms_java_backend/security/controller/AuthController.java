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
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/${api.base.path}/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthService authService;
    public final JwtService jwtService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse> login(@RequestBody LoginRequest loginRequest, HttpServletRequest request) {
        log.info("Received login attempt for email: {}", loginRequest.getEmail());

        ApiResponse response = authService.authenticateUser(loginRequest);

        if (response.getSuccess() == 1) {
            log.info("Login successful for user: {}", loginRequest.getEmail());
        } else {
            log.warn("Login failed for user: {}", loginRequest.getEmail());
        }

        return ResponseUtil.buildResponse(request, response, 0L);
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse> logout(
            @RequestHeader(value = "Authorization", required = false) String accessToken,
            HttpServletRequest request) {
           log.info("Received logout request");
            ApiResponse response=authService.logout(accessToken);
        if (response.getSuccess() == 1) {
            log.info("User logout successfully");
        } else {
            log.warn("Logout failed for security reason");
        }
            return ResponseUtil.buildResponse(request, response, 0L);

    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse> register(@Validated @RequestBody RegisterRequest registerRequest,
                                                HttpServletRequest request) {
        log.info("Received registration request for email: {}", registerRequest.getEmail());

        ApiResponse response = authService.registerUser(registerRequest);

        if (response.getSuccess() == 1) {
            log.info("User registered successfully: {}", registerRequest.getEmail());
        } else {
            log.warn("Registration failed for email: {}", registerRequest.getEmail());
        }

        return ResponseUtil.buildResponse(request, response, 0L);
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse> refresh(@Validated @RequestBody RefreshTokenData refreshTokenData,
                                               HttpServletRequest request) {
        log.info("Received token refresh request");

        ApiResponse response = authService.refreshToken(refreshTokenData.getRefreshToken());

        if (response.getSuccess() == 1) {
            log.info("Token refreshed successfully");
        } else {
            log.warn("Token refresh failed");
        }

        return ResponseUtil.buildResponse(request, response, 0L);
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse> getCurrentUser(@RequestHeader("Authorization") String authHeader,
                                                      HttpServletRequest request) {
        log.info("Fetching current authenticated user");

        double requestStartTime = System.currentTimeMillis();
        ApiResponse response = authService.getCurrentUser(authHeader);

        return ResponseUtil.buildResponse(request, response, requestStartTime);
    }
}
