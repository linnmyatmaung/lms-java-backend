/*
 * @Author : Linn Myat Maung
 * @Date   : 4/13/2025
 * @Time   : 5:34 PM
 */

package com.lucus.lms_java_backend.api.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * Data Transfer Object for creating a new user.
 */
@Data
public class CreateAdminRequest {

    @NotBlank(message = "Name is required.")
    @Size(min = 3, max = 50, message = "Name must be between 3 and 50 characters.")
    private String name;

    @NotBlank(message = "Email is required.")
    @Email(message = "Email should be valid.")
    private String email;

    @NotBlank(message = "Password is required.")
    @Size(min = 8, message = "Password must be at least 8 characters.")
    private String password;

    @NotBlank(message = "Department is required.")
    @Size(min = 3, max = 50, message = "Department must be between 3 and 50 characters.")
    private String department;

}