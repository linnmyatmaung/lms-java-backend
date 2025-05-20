/*
 * @Author : Linn Myat Maung
 * @Date   : 5/15/2025
 * @Time   : 3:47 PM
 */

package com.lucus.lms_java_backend.api.category.repository;


import com.lucus.lms_java_backend.api.category.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}