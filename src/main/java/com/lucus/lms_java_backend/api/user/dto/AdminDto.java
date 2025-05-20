/*
 * @Author : Linn Myat Maung
 * @Date   : 4/10/2025
 * @Time   : 3:54 PM
 */

package com.lucus.lms_java_backend.api.user.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminDto {
    private Long id;
    private String name;
    private String email;
    private String username;
    private String department;
    private String createdAt;
    private String updatedAt;
}
