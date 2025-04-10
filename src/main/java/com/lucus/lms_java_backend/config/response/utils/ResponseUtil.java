/*
 * @Author : Linn Myat Maung
 * @Date   : 4/10/2025
 * @Time   : 2:10 PM
 */

package com.lucus.lms_java_backend.config.response.utils;

import jakarta.servlet.http.HttpServletRequest;
import com.lucus.lms_java_backend.config.response.dto.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.Instant;
import java.util.HashMap;

public class ResponseUtil {

    public static ResponseEntity<ApiResponse> buildResponse(HttpServletRequest request, ApiResponse response, double requestTime) {
        HttpStatus status = HttpStatus.valueOf(response.getCode());

        if (response.getMeta() == null) {
            String method = request.getMethod();
            String endpoint = request.getRequestURI();
            response.setMeta(new HashMap<>());
            response.getMeta().put("method", method);
            response.getMeta().put("endpoint", endpoint);
        }

        response.setDuration(Instant.now().getEpochSecond() - requestTime);
        return new ResponseEntity<>(response, status);
    }
}