/*
 * @Author : Linn Myat Maung
 * @Date   : 4/14/2025
 * @Time   : 9:10 AM
 */

package com.lucus.lms_java_backend.api.user.repository;


import com.lucus.lms_java_backend.api.role.model.RoleName;
import com.lucus.lms_java_backend.api.user.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    @Query(value = "SELECT * FROM user u LIMIT :limit OFFSET :offset", nativeQuery = true)
    List<User> findUsersWithPagination(@Param("offset") int offset, @Param("limit") int limit);

    @Query("SELECT COUNT(u) FROM User u")
    long countUsers();

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String userEmail);

    boolean existsByUsername(String username);

    @Query(value = "SELECT COUNT(*) FROM user WHERE username = :username", nativeQuery = true)
    int countByUsername(@Param("username") String username);
    // Get users who have the 'USER' role with pagination
    @Query("SELECT u FROM User u JOIN u.roles r WHERE r.name = :roleName")
  Page<User> findUsersByRoleName(@Param("roleName") RoleName roleName, Pageable pageable);
}
