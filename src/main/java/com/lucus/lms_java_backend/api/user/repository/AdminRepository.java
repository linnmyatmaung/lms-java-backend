/*
 * @Author : Linn Myat Maung
 * @Date   : 4/14/2025
 * @Time   : 9:15 AM
 */

package com.lucus.lms_java_backend.api.user.repository;

import com.lucus.lms_java_backend.api.user.model.Admin;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AdminRepository extends JpaRepository<Admin, Long> {

    Page<Admin> findAll(Pageable pageable);

    @Query("SELECT COUNT(a) FROM Admin a")
    long countAdmins();

    Optional<Admin> findByEmail(String email); // inherited field

    boolean existsByEmail(String email);       // inherited field
}
