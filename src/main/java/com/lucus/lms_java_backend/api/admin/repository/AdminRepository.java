/*
 * @Author : Linn Myat Maung
 * @Date   : 5/13/2025
 * @Time   : 5:04 PM
 */

package com.lucus.lms_java_backend.api.admin.repository;

import com.lucus.lms_java_backend.api.admin.model.Admin;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminRepository extends JpaRepository<Admin, Long> {
}