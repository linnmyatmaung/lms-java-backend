/*
 * @Author : Linn Myat Maung
 * @Date   : 4/14/2025
 * @Time   : 3:46 PM
 */

package com.lucus.lms_java_backend.security.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import java.security.Key;
import java.util.Date;
import java.util.Map;

public class JwtUtil {

    private static final String SECRET = "bh5pYpZP4QuAlHFF4NljKIQD9QWU8HmX2wyKAiBaArk=";
    private static final Key SECRET_KEY;

    static {
        if (SECRET == null || SECRET.isEmpty()) {
            throw new IllegalStateException("JWT_SECRET_KEY environment variable is missing or empty!");
        }
        SECRET_KEY = Keys.hmacShaKeyFor(SECRET.getBytes());
    }

    private static final String ISSUER = "teamSmurfs-backend";

    public static String generateToken(Map<String, Object> claims, String role, String subject, long expirationMillis) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuer(ISSUER)
                .claim("role", role)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationMillis))
                .signWith(SECRET_KEY)
                .compact();
    }

    public static Claims decodeToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(SECRET_KEY)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public static boolean isTokenValid(String token) {
        try {
            Claims claims = decodeToken(token);

            if (claims.getExpiration().before(new Date())) {
                return false;
            }

            if (!ISSUER.equals(claims.getIssuer())) {
                return false;
            }

            return claims.getSubject() != null && !claims.getSubject().isEmpty();
        } catch (ExpiredJwtException e) {
            return false; // Token expired
        } catch (JwtException e) {
            return false; // Invalid token (e.g., signature tampered)
        }
    }
}
