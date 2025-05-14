/*
 * @Author : Linn Myat Maung
 * @Date   : 4/14/2025
 * @Time   : 9:13 AM
 */

package com.lucus.lms_java_backend.api.user.service;


import com.lucus.lms_java_backend.api.user.dto.CreateAdminRequest;
import com.lucus.lms_java_backend.config.response.dto.ApiResponse;


public interface AdminService {
    Object retrieveAdmins(int page, int limit) throws Exception;

    Object createAdmin(CreateAdminRequest createAdminRequest) throws Exception;

}