/*
 * @Author : Linn Myat Maung
 * @Date   : 4/14/2025
 * @Time   : 4:16 PM
 */

package com.lucus.lms_java_backend.security.service.impl;


import com.lucus.lms_java_backend.security.service.JwtService;
import com.lucus.lms_java_backend.security.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class JwtServiceImpl implements JwtService {

    private final Set<String> revokedTokens = ConcurrentHashMap.newKeySet();

    @Override
    public Claims validateToken(String token) {
        if (!JwtUtil.isTokenValid(token)) {
            throw new SecurityException("Invalid or expired token.");
        }

        if (isTokenRevoked(token)) {
            throw new SecurityException("Token has been revoked.");
        }

        return JwtUtil.decodeToken(token);
    }

    @Override
    public void revokeToken(String token) {
        revokedTokens.add(token);
    }

    private boolean isTokenRevoked(String token) {
        return revokedTokens.contains(token);
    }

    @Override
    public String generateToken(Map<String, Object> claims, String roleName, String subject, long expirationMillis) {
        return JwtUtil.generateToken(claims, roleName, subject, expirationMillis);
    }
}