/*
 * @Author : Linn Myat Maung
 * @Date   : 4/13/2025
 * @Time   : 5:38 PM
 */

package com.lucus.lms_java_backend.api.user.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChangePasswordRequest {
    @NotBlank(message = "Old password cannot be empty")
    private String oldPassword;

    @NotBlank(message = "New password cannot be empty")
    private String newPassword;
}
