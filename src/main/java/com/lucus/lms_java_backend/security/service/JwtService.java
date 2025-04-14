/*
 * @Author : Linn Myat Maung
 * @Date   : 4/14/2025
 * @Time   : 4:06 PM
 */

package com.lucus.lms_java_backend.security.service;


import io.jsonwebtoken.Claims;

import java.util.Map;

public interface JwtService {
    Claims validateToken(String token);

    void revokeToken(String token);

    String generateToken(Map<String, Object> claims, String roleName, String subject, long expirationMillis);
}