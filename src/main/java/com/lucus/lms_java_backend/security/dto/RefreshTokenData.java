/*
 * @Author : Linn Myat Maung
 * @Date   : 4/14/2025
 * @Time   : 3:34 PM
 */

package com.lucus.lms_java_backend.security.dto;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RefreshTokenData {
    private String refreshToken;
}