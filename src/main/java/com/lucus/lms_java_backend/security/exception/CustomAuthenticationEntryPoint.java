/*
 * @Author : Linn Myat Maung
 * @Date   : 4/14/2025
 * @Time   : 3:38 PM
 */

package com.lucus.lms_java_backend.security.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
        Long startTime = (Long) request.getAttribute("startTime");
        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("success", 0);
        responseBody.put("code", HttpServletResponse.SC_UNAUTHORIZED);
        responseBody.put("meta", Map.of(
                "method", request.getMethod(),
                "endpoint", request.getRequestURI()
        ));
        responseBody.put("data", "Security violation.");
        responseBody.put("message", "Invalid or expired token.");
        long duration = (startTime != null) ? System.currentTimeMillis() - startTime : 0;
        responseBody.put("duration", duration);  // duration in milliseconds

        response.getWriter().write(objectMapper.writeValueAsString(responseBody));
    }

}
