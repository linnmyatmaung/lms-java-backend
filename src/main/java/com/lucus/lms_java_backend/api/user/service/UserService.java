/*
 * @Author : Linn Myat Maung
 * @Date   : 4/14/2025
 * @Time   : 9:13 AM
 */

package com.lucus.lms_java_backend.api.user.service;


import com.lucus.lms_java_backend.api.user.dto.CreateUserRequest;

public interface UserService {
    Object retrieveUsers(int page, int limit) throws Exception;

    Object createUser(CreateUserRequest createUserRequest) throws Exception;

    void changePassword(String oldPassword, String newPassword, String authHeader) throws Exception;

    boolean usernameExists(String username);
}
