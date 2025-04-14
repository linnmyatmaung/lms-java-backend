/*
 * @Author : Linn Myat Maung
 * @Date   : 4/14/2025
 * @Time   : 8:55 AM
 */

package com.lucus.lms_java_backend.api.user.utils;


import java.util.regex.Pattern;

public class PasswordValidatorUtil {
    private static final String PASSWORD_REGEX =
            "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$";

    private static final Pattern PASSWORD_PATTERN = Pattern.compile(PASSWORD_REGEX);

    public static boolean isValid(String password) {
        return PASSWORD_PATTERN.matcher(password).matches();
    }
}
