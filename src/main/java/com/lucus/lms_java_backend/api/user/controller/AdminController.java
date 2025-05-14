/*
 * @Author : Linn Myat Maung
 * @Date   : 5/14/2025
 * @Time   : 10:00 AM
 */

package com.lucus.lms_java_backend.api.user.controller;

import com.lucus.lms_java_backend.api.user.dto.CreateAdminRequest;
import com.lucus.lms_java_backend.api.user.service.AdminService;
import com.lucus.lms_java_backend.config.response.dto.ApiResponse;
import com.lucus.lms_java_backend.config.response.dto.PaginatedResponse;
import com.lucus.lms_java_backend.config.response.utils.ResponseUtil;
import com.lucus.lms_java_backend.config.utils.PaginationMetaUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/${api.base.path}/${api.admin.base.path}")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Admin", description = "Admin API")
public class AdminController {

    private final AdminService adminService;

    /**
     * Create a new admin user.
     *
     * @param createAdminRequest request containing admin data
     * @param request            HttpServletRequest
     * @return ResponseEntity with the created admin data
     */
    @PostMapping
    @Operation(summary = "Create new admin")
    public ResponseEntity<ApiResponse> createAdmin(
            @Valid @RequestBody CreateAdminRequest createAdminRequest,
            HttpServletRequest request
    ) throws Exception {

        log.info("Received request to create admin: {}", createAdminRequest.getEmail());

        Object createdAdmin = adminService.createAdmin(createAdminRequest);

        ApiResponse response = ApiResponse.builder()
                .success(1)
                .code(HttpStatus.CREATED.value())
                .data(createdAdmin)
                .message("Admin created successfully")
                .build();

        return ResponseUtil.buildResponse(request, response, 0L);
    }

    /**
     * Retrieve paginated list of admins.
     *
     * @param request HttpServletRequest
     * @param page    page number
     * @param limit   number of records per page
     * @return paginated list of admins
     */
    @GetMapping
    @Operation(summary = "Retrieve all admins")
    public ResponseEntity<ApiResponse> retrieveAdmins(
            HttpServletRequest request,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "limit", defaultValue = "10") int limit
    ) throws Exception {

        log.info("Fetching admins - Page: {}, Limit: {}", page, limit);

        Object paginatedAdmins = adminService.retrieveAdmins(page, limit);
        Map<String, Object> meta = PaginationMetaUtil.buildPaginationMeta(request, page, limit, paginatedAdmins);

        Object data = (paginatedAdmins instanceof PaginatedResponse<?>)
                ? ((PaginatedResponse<?>) paginatedAdmins).getItems()
                : Collections.emptyList();

        log.info("Admins retrieved: {}", (data != null) ? ((List<?>) data).size() : 0);

        ApiResponse response = ApiResponse.builder()
                .success(1)
                .code(HttpStatus.OK.value())
                .data(data)
                .meta(meta)
                .message("Admins retrieved successfully")
                .build();

        return ResponseUtil.buildResponse(request, response, 0L);
    }
}
