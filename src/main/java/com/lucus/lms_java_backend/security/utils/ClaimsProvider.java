/*
 * @Author : Linn Myat Maung
 * @Date   : 4/14/2025
 * @Time   : 3:42 PM
 */

package com.lucus.lms_java_backend.security.utils;




import com.lucus.lms_java_backend.api.user.model.User;

import java.util.HashMap;
import java.util.Map;

public class ClaimsProvider {

    private ClaimsProvider() {
        throw new IllegalStateException("Utility class");
    }

    public static Map<String, Object> generateClaims(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("id", user.getId());
        claims.put("email", user.getEmail());
        claims.put("role", "USER");
        return claims;
    }
}
