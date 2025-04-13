/*
 * @Author : Linn Myat Maung
 * @Date   : 4/10/2025
 * @Time   : 3:11 PM
 */

package com.lucus.lms_java_backend.api.role.repository;


import com.lucus.lms_java_backend.api.role.model.Role;
import com.lucus.lms_java_backend.api.role.model.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(RoleName name);
}
