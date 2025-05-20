/*
 * @Author : Linn Myat Maung
 * @Date   : 5/15/2025
 * @Time   : 4:08 PM
 */

package com.lucus.lms_java_backend.api.category.service.impl;

import com.lucus.lms_java_backend.api.category.dto.CategoryRequest;
import com.lucus.lms_java_backend.api.category.dto.CategoryResponse;
import com.lucus.lms_java_backend.api.category.model.Category;
import com.lucus.lms_java_backend.api.category.repository.CategoryRepository;
import com.lucus.lms_java_backend.api.category.service.CategoryService;
import com.lucus.lms_java_backend.config.utils.DtoUtil;
import com.lucus.lms_java_backend.config.utils.EntityUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import java.util.List;
@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final ModelMapper modelMapper;

    @Override
    @Transactional
    public CategoryResponse createCategory(CategoryRequest request){
        Category category= DtoUtil.map(request, Category.class,modelMapper);
        Category savedCategory = EntityUtil.saveEntity(categoryRepository,category,"Category");
        log.info("Saved category: {}", savedCategory);
        return DtoUtil.map(savedCategory, CategoryResponse.class,modelMapper);
    }


    @Override
    public List<CategoryResponse> getAllCategories(){
        List<Category> categories = EntityUtil.getAllEntities(categoryRepository, Sort.by("id").ascending(), "Category");
        return DtoUtil.mapList(categories, CategoryResponse.class,modelMapper);
    }


    @Override
    public CategoryResponse getCategoryById(Long categoryId){
        Category category = EntityUtil.getEntityById(categoryRepository,categoryId,"Category");
        return DtoUtil.map(category, CategoryResponse.class,modelMapper);
    }

    @Override
    @Transactional
    public CategoryResponse updateCategory(Long categoryId, CategoryRequest updateRequest) {
        Category existingCategory = EntityUtil.getEntityById(categoryRepository, categoryId, "Category");
        existingCategory.setName(updateRequest.getName());
        Category savedCategory = EntityUtil.saveEntity(categoryRepository, existingCategory, "Category");
        return DtoUtil.map(savedCategory, CategoryResponse.class, modelMapper);
    }


    @Override
    @Transactional
    public void deleteCategory(Long categoryId){
        EntityUtil.deleteEntity(categoryRepository,categoryId, "Category");
    }
}
