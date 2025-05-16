/*
 * @Author : Linn Myat Maung
 * @Date   : 5/15/2025
 * @Time   : 3:53 PM
 */

package com.lucus.lms_java_backend.api.category.dto;


import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CategoryRequest {
    @NotBlank(message = "Category name is required")
    private String name;
}