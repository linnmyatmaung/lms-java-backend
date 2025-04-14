/*
 * @Author : Linn Myat Maung
 * @Date   : 4/14/2025
 * @Time   : 3:50 PM
 */

package com.lucus.lms_java_backend.security.service;


import com.lucus.lms_java_backend.config.response.dto.ApiResponse;
import com.lucus.lms_java_backend.security.dto.LoginRequest;
import com.lucus.lms_java_backend.security.dto.RegisterRequest;

public interface AuthService {
    ApiResponse authenticateUser(LoginRequest loginRequest);

    ApiResponse registerUser(RegisterRequest registerRequest);

    void logout(String accessToken);

    ApiResponse refreshToken(String refreshToken);

    ApiResponse getCurrentUser(String authHeader);
}
