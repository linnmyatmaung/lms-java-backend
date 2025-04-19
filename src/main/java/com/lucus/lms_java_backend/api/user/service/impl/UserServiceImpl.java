/*
 * @Author : Linn Myat Maung
 * @Date   : 4/14/2025
 * @Time   : 9:20 AM
 */

package com.lucus.lms_java_backend.api.user.service.impl;


import com.lucus.lms_java_backend.api.role.model.Role;
import com.lucus.lms_java_backend.api.role.model.RoleName;
import com.lucus.lms_java_backend.api.role.repository.RoleRepository;
import com.lucus.lms_java_backend.api.user.dto.CreateUserRequest;
import com.lucus.lms_java_backend.api.user.dto.UserDto;
import com.lucus.lms_java_backend.api.user.model.User;
import com.lucus.lms_java_backend.api.user.repository.UserRepository;
import com.lucus.lms_java_backend.api.user.service.UserService;
import com.lucus.lms_java_backend.api.user.utils.PasswordValidatorUtil;
import com.lucus.lms_java_backend.api.user.utils.UserUtil;
import com.lucus.lms_java_backend.config.exception.DuplicateEntityException;
import com.lucus.lms_java_backend.config.response.dto.ApiResponse;
import com.lucus.lms_java_backend.config.response.dto.PaginatedResponse;
import com.lucus.lms_java_backend.config.utils.DtoUtil;
import com.lucus.lms_java_backend.config.utils.EntityUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j

public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;
    private final UserUtil userUtil;

    @Override
    public Object retrieveUsers(int page, int limit) throws Exception {
        try {
            log.info("Fetching users from database with page: {}, limit: {}", page, limit);

            int offset = (page - 1) * limit;
            List<User> users = userRepository.findUsersWithPagination(offset, limit);

            long totalItems = userRepository.countUsers();
            int lastPage = (int) Math.ceil((double) totalItems / limit);

            List<UserDto> userList = DtoUtil.mapList(users, UserDto.class, modelMapper);

            log.info("Fetched {} users, total users in system: {}", users.size(), totalItems);

            return PaginatedResponse.<UserDto>builder()
                    .items(userList != null ? userList : Collections.emptyList())
                    .totalItems(totalItems)
                    .lastPage(lastPage)
                    .build();
        } catch (Exception e) {
            log.error("Error retrieving users: {}", e.getMessage());
            throw new Exception("Error retrieving users: " + e.getMessage());
        }
    }

    @Override
    public Object createUser(CreateUserRequest createUserRequest) throws Exception {
        try {
            log.info("Creating new user with email: {}", createUserRequest.getEmail());

            if (userRepository.findByEmail(createUserRequest.getEmail()).isPresent()) {
                log.warn("Email already exists: {}", createUserRequest.getEmail());
                throw new DuplicateEntityException("Email already exists: " + createUserRequest.getEmail());
            }

            User user = modelMapper.map(createUserRequest, User.class);
            String uniqueUsername = userUtil.generateUniqueUsername(createUserRequest.getName());
            user.setUsername(uniqueUsername);

            user.setPassword(passwordEncoder.encode(createUserRequest.getPassword()));

            Role userRole = roleRepository.findByName(RoleName.ROLE_USER)
                    .orElseThrow(() -> new RuntimeException("Role not found in database!"));
            log.info("Assigning role: {}", userRole.getName());
            user.setRoles(Set.of(userRole));

            User savedUser = userRepository.save(user);

            log.info("User created successfully with ID: {}", savedUser.getId());

            return modelMapper.map(savedUser, UserDto.class);
        } catch (DuplicateEntityException e) {
            throw e;
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    @Override
    @Transactional
    public void changePassword(String oldPassword, String newPassword, String authHeader) throws Exception {
        log.info("Initiating password change for authenticated user.");

        UserDto userDto = userUtil.getCurrentUserDto(authHeader);
        User currentUser = EntityUtil.getEntityById(userRepository, userDto.getId());

        if (!passwordEncoder.matches(oldPassword, currentUser.getPassword())) {
            log.warn("Password change failed: Incorrect old password for user ID {}", currentUser.getId());
            throw new IllegalArgumentException("Incorrect old password.");
        }

        if (!PasswordValidatorUtil.isValid(newPassword)) {
            log.warn("Password change failed: Weak password provided.");
            throw new IllegalArgumentException("Password does not meet security requirements.");
        }

        currentUser.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(currentUser);

        log.info("Password changed successfully for user ID {}", currentUser.getId());
    }

    @Override
    public boolean usernameExists(String username) {
        return userRepository.countByUsername(username) > 0;
    }
}
