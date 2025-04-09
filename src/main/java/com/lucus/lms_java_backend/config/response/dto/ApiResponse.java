/*
 * @Author : Linn Myat Maung
 * @Date   : 4/9/2025
 * @Time   : 6:32 PM
 */

package com.lucus.lms_java_backend.config.response.dto;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
public class ApiResponse {
    private int success;
    private int code;
    private Map<String, Object> meta;
    private Object data;
    private String message;
    private double duration;
}