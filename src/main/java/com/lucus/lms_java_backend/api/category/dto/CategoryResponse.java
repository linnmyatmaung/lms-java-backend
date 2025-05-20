/*
 * @Author : Linn Myat Maung
 * @Date   : 5/15/2025
 * @Time   : 3:54 PM
 */

package com.lucus.lms_java_backend.api.category.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryResponse {
    private Long id;
    private String name;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}