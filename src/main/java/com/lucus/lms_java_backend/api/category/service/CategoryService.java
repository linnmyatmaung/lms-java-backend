/*
 * @Author : Linn Myat Maung
 * @Date   : 5/15/2025
 * @Time   : 3:49 PM
 */

package com.lucus.lms_java_backend.api.category.service;

import com.lucus.lms_java_backend.api.category.dto.CategoryResponse;
import com.lucus.lms_java_backend.api.category.dto.CategoryRequest;

import java.util.List;

public interface CategoryService {
    CategoryResponse createCategory(CategoryRequest CategoryRequest);
    List<CategoryResponse> getAllCategories();
    CategoryResponse getCategoryById(Long categoryId);
    CategoryResponse updateCategory(Long categoryId, CategoryRequest updateRequest);
    void deleteCategory(Long categoryId);
}