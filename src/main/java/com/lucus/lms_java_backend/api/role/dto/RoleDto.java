/*
 * @Author : Linn Myat Maung
 * @Date   : 4/10/2025
 * @Time   : 2:53 PM
 */

package com.lucus.lms_java_backend.api.role.dto;


import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RoleDto {
    private Long id;
    private String name;
}
