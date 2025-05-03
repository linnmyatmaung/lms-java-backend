/*
 * @Author : Linn Myat Maung
 * @Date   : 4/14/2025
 * @Time   : 9:20 AM
 */
package com.lucus.lms_java_backend.api.user.service.impl;

import com.lucus.lms_java_backend.api.role.model.Role;
import com.lucus.lms_java_backend.api.role.model.RoleName;
import com.lucus.lms_java_backend.api.role.repository.RoleRepository;
import com.lucus.lms_java_backend.api.token.model.Token;
import com.lucus.lms_java_backend.api.token.repository.TokenRepository;
import com.lucus.lms_java_backend.api.user.dto.CreateUserRequest;
import com.lucus.lms_java_backend.api.user.dto.UserDto;
import com.lucus.lms_java_backend.api.user.model.User;
import com.lucus.lms_java_backend.api.user.repository.UserRepository;
import com.lucus.lms_java_backend.api.user.service.UserService;
import com.lucus.lms_java_backend.api.user.utils.PasswordValidatorUtil;
import com.lucus.lms_java_backend.api.user.utils.UserUtil;
import com.lucus.lms_java_backend.config.exception.DuplicateEntityException;
import com.lucus.lms_java_backend.config.response.dto.PaginatedResponse;
import com.lucus.lms_java_backend.config.utils.DtoUtil;
import com.lucus.lms_java_backend.config.utils.EntityUtil;
import com.lucus.lms_java_backend.security.service.JwtService;
import com.lucus.lms_java_backend.security.utils.ClaimsProvider;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static java.util.Objects.requireNonNull;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private static final long ACCESS_TOKEN_EXPIRATION_MS = 15 * 60 * 1000;
    private static final long REFRESH_TOKEN_EXPIRATION_MS = 7 * 24 * 60 * 60 * 1000;

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final TokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;
    private final JwtService jwtService;
    private final UserUtil userUtil;

    @Override
    public PaginatedResponse<UserDto> retrieveUsers(int page, int limit) {
        log.info("Fetching users - page: {}, limit: {}", page, limit);

        int offset = (page - 1) * limit;
        List<User> users = userRepository.findUsersWithPagination(offset, limit);
        long totalItems = userRepository.countUsers();
        int lastPage = (int) Math.ceil((double) totalItems / limit);

        List<UserDto> userDtos = DtoUtil.mapList(users, UserDto.class, modelMapper);

        return PaginatedResponse.<UserDto>builder()
                .items(userDtos != null ? userDtos : Collections.emptyList())
                .totalItems(totalItems)
                .lastPage(lastPage)
                .build();
    }

    @Override
    @Transactional
    public UserDto createUser(CreateUserRequest request) {
        log.info("Creating user with email: {}", request.getEmail());

        userRepository.findByEmail(request.getEmail())
                .ifPresent(u -> {
                    throw new DuplicateEntityException("Email already exists: " + request.getEmail());
                });

        validatePasswordStrength(request.getPassword());

        User user = buildNewUser(request);
        Role defaultRole = getDefaultUserRole();

        user.setRoles(Set.of(defaultRole));
        User savedUser = userRepository.save(user);

        Map<String, String> tokens = generateTokens(savedUser, defaultRole.getName().name());
        saveRefreshToken(savedUser, tokens.get("refreshToken"));

        log.info("User created successfully - ID: {}", savedUser.getId());

        return DtoUtil.map(savedUser, UserDto.class, modelMapper);
    }

    @Override
    @Transactional
    public void changePassword(String oldPassword, String newPassword, String authHeader) {
        log.info("Changing password for current user.");

        UserDto currentUserDto = userUtil.getCurrentUserDto(authHeader);
        User currentUser = EntityUtil.getEntityById(userRepository, currentUserDto.getId());

        if (!passwordEncoder.matches(oldPassword, currentUser.getPassword())) {
            throw new IllegalArgumentException("Incorrect old password.");
        }

        validatePasswordStrength(newPassword);

        currentUser.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(currentUser);

        log.info("Password changed successfully - User ID: {}", currentUser.getId());
    }

    @Override
    public boolean usernameExists(String username) {
        return userRepository.countByUsername(username) > 0;
    }

    // --- Private utility methods ---

    private void validatePasswordStrength(String password) {
        if (!PasswordValidatorUtil.isValid(password)) {
            throw new IllegalArgumentException("Password must include uppercase, lowercase, number, and special character.");
        }
    }

    private User buildNewUser(CreateUserRequest request) {
        User user = modelMapper.map(request, User.class);
        user.setUsername(userUtil.generateUniqueUsername(request.getName()));
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        return user;
    }

    private Role getDefaultUserRole() {
        return roleRepository.findByName(RoleName.ROLE_USER)
                .orElseThrow(() -> new IllegalStateException("Default role not found."));
    }

    private Map<String, String> generateTokens(User user, String roleName) {
        String accessToken = jwtService.generateToken(ClaimsProvider.generateClaims(user), roleName, user.getEmail(), ACCESS_TOKEN_EXPIRATION_MS);
        String refreshToken = jwtService.generateToken(ClaimsProvider.generateClaims(user), roleName, user.getEmail(), REFRESH_TOKEN_EXPIRATION_MS);
        return Map.of("accessToken", accessToken, "refreshToken", refreshToken);
    }

    private void saveRefreshToken(User user, String refreshToken) {
        Token token = Token.builder()
                .user(user)
                .refreshtoken(refreshToken)
                .expiredAt(Instant.now().plus(7, ChronoUnit.DAYS))
                .build();
        tokenRepository.save(token);
    }
}
