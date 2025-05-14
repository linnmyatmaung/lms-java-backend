/*
 * @Author : Linn Myat Maung
 * @Date   : 5/14/2025
 * @Time   : 9:00 AM
 */

package com.lucus.lms_java_backend.api.user.service.impl;

import com.lucus.lms_java_backend.api.role.model.Role;
import com.lucus.lms_java_backend.api.role.model.RoleName;
import com.lucus.lms_java_backend.api.role.repository.RoleRepository;
import com.lucus.lms_java_backend.api.token.model.Token;
import com.lucus.lms_java_backend.api.token.repository.TokenRepository;
import com.lucus.lms_java_backend.api.user.dto.AdminDto;
import com.lucus.lms_java_backend.api.user.dto.CreateAdminRequest;
import com.lucus.lms_java_backend.api.user.model.Admin;
import com.lucus.lms_java_backend.api.user.repository.AdminRepository;
import com.lucus.lms_java_backend.api.user.service.AdminService;
import com.lucus.lms_java_backend.api.user.utils.PasswordValidatorUtil;
import com.lucus.lms_java_backend.api.user.utils.UserUtil;
import com.lucus.lms_java_backend.config.exception.DuplicateEntityException;
import com.lucus.lms_java_backend.config.response.dto.PaginatedResponse;
import com.lucus.lms_java_backend.config.utils.DtoUtil;
import com.lucus.lms_java_backend.security.service.JwtService;
import com.lucus.lms_java_backend.security.utils.ClaimsProvider;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminServiceImpl implements AdminService {

    private static final long ACCESS_TOKEN_EXPIRATION_MS = 15 * 60 * 1000;
    private static final long REFRESH_TOKEN_EXPIRATION_MS = 7 * 24 * 60 * 60 * 1000;

    private final AdminRepository adminRepository;
    private final RoleRepository roleRepository;
    private final TokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;
    private final JwtService jwtService;
    private final UserUtil userUtil;

    @Override
    public PaginatedResponse<AdminDto> retrieveAdmins(int page, int limit) {
        log.info("Fetching admins - page: {}, limit: {}", page, limit);

        Pageable pageable = PageRequest.of(page - 1, limit);
        Page<Admin> adminPage = adminRepository.findAll(pageable);
        List<Admin> admins = adminPage.getContent();
        long totalItems = adminPage.getTotalElements();
        int lastPage = adminPage.getTotalPages();

        List<AdminDto> adminDtos = DtoUtil.mapList(admins, AdminDto.class, modelMapper);

        return PaginatedResponse.<AdminDto>builder()
                .items(adminDtos != null ? adminDtos : Collections.emptyList())
                .totalItems(totalItems)
                .lastPage(lastPage)
                .build();
    }

    @Override
    @Transactional
    public AdminDto createAdmin(CreateAdminRequest request) {
        log.info("Creating admin with email: {}", request.getEmail());

        adminRepository.findByEmail(request.getEmail())
                .ifPresent(u -> {
                    throw new DuplicateEntityException("Email already exists: " + request.getEmail());
                });

        validatePasswordStrength(request.getPassword());

        Admin admin = buildNewAdmin(request);
        Role defaultRole = getDefaultAdminRole();

        admin.setRoles(Set.of(defaultRole));
        Admin savedAdmin = adminRepository.save(admin);

        Map<String, String> tokens = generateTokens(savedAdmin, defaultRole.getName().name());
        saveRefreshToken(savedAdmin, tokens.get("refreshToken"));

        log.info("Admin created successfully - ID: {}", savedAdmin.getId());

        return DtoUtil.map(savedAdmin, AdminDto.class, modelMapper);
    }




    // --- Private utility methods ---

    private void validatePasswordStrength(String password) {
        if (!PasswordValidatorUtil.isValid(password)) {
            throw new IllegalArgumentException("Password must include uppercase, lowercase, number, and special character.");
        }
    }

    private Admin buildNewAdmin(CreateAdminRequest request) {
        Admin admin = modelMapper.map(request, Admin.class);
        admin.setUsername(userUtil.generateUniqueUsername(request.getName()));
        admin.setPassword(passwordEncoder.encode(request.getPassword()));
        admin.setDepartment(request.getDepartment());
        return admin;
    }

    private Role getDefaultAdminRole() {
        return roleRepository.findByName(RoleName.ROLE_ADMIN)
                .orElseThrow(() -> new IllegalStateException("Default admin role not found."));
    }

    private Map<String, String> generateTokens(Admin admin, String roleName) {
        String accessToken = jwtService.generateToken(
                ClaimsProvider.generateClaims(admin),
                roleName,
                admin.getEmail(),
                ACCESS_TOKEN_EXPIRATION_MS
        );
        String refreshToken = jwtService.generateToken(
                ClaimsProvider.generateClaims(admin),
                roleName,
                admin.getEmail(),
                REFRESH_TOKEN_EXPIRATION_MS
        );
        return Map.of("accessToken", accessToken, "refreshToken", refreshToken);
    }

    private void saveRefreshToken(Admin admin, String refreshToken) {
        Token token = Token.builder()
                .user(admin)
                .refreshtoken(refreshToken)
                .expiredAt(Instant.now().plus(7, ChronoUnit.DAYS))
                .build();
        tokenRepository.save(token);
    }
}
