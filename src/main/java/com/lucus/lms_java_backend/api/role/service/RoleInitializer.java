/*
 * @Author : Linn Myat Maung
 * @Date   : 4/10/2025
 * @Time   : 3:42 PM
 */

package com.lucus.lms_java_backend.api.role.service;


import com.lucus.lms_java_backend.api.role.model.Role;
import com.lucus.lms_java_backend.api.role.model.RoleName;
import com.lucus.lms_java_backend.api.role.repository.RoleRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class RoleInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;

    @Override
    @Transactional
    public void run(String... args) {
        log.info("Initializing roles...");

        Set<RoleName> existingRoles = roleRepository.findAll()
                .stream()
                .map(Role::getName)
                .collect(Collectors.toSet());

        Arrays.stream(RoleName.values())
                .filter(roleName -> !existingRoles.contains(roleName))
                .forEach(this::createRoleIfNotExists);

        log.info("Role initialization completed.");
    }

    private void createRoleIfNotExists(RoleName roleName) {
        if (roleRepository.findByName(roleName).isEmpty()) {
            Role role = new Role();
            role.setName(roleName);
            roleRepository.save(role);
            log.info("Inserted role: {}", roleName);
        }
    }
}
