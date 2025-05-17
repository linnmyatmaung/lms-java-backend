/*
 * @Author : Linn Myat Maung
 * @Date   : 5/17/2025
 * @Time   : 9:31 AM
 */

package com.lucus.lms_java_backend.api.category.controller;

import com.lucus.lms_java_backend.api.category.dto.CategoryRequest;
import com.lucus.lms_java_backend.api.category.service.CategoryService;
import com.lucus.lms_java_backend.config.response.dto.ApiResponse;
import com.lucus.lms_java_backend.config.response.utils.ResponseUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/${api.base.path}/${api.category.base.path}")
@RequiredArgsConstructor
@Tag(name = "Category", description = "Category API")
@Slf4j
public class CategoryController {

   private final CategoryService categoryService;

   @PostMapping
   @Operation(summary = "Create new Category")
    public ResponseEntity<ApiResponse> createCategory(
            @Valid @RequestBody CategoryRequest createRequest, HttpServletRequest request) throws Exception{
       log.info("Received request to create category: {}", createRequest.getName());
       Object createdCategory = categoryService.createCategory(createRequest);
       ApiResponse response = ApiResponse.builder()
               .success(1)
               .code(HttpStatus.CREATED.value())
               .data(createdCategory)
               .message("Category created successfully")
               .build();
       return ResponseUtil.buildResponse(request, response, 0L);
   }

   @GetMapping
   @Operation(summary = "Get all categories")
   public ResponseEntity<ApiResponse> getCategories(HttpServletRequest request){
      Object category = categoryService.getAllCategories();
      ApiResponse response = ApiResponse.builder()
              .success(1)
              .code(HttpStatus.OK.value())
              .data(category)
              .message("Categories retrieved successfully")
              .build();
      return ResponseUtil.buildResponse( request , response, 0L);
   }

   @GetMapping("/{id}")
   @Operation(summary = "Get category by Id")
   public ResponseEntity<ApiResponse> getCategoryById(@PathVariable("id") Long id, HttpServletRequest request){
      Object category = categoryService.getCategoryById(id);
      ApiResponse response = ApiResponse.builder()
              .success(1)
              .code(HttpStatus.OK.value())
              .data(category)
              .message("Category retrieved successfully")
              .build();
      return ResponseUtil.buildResponse(request , response, 0L);
   }

   @PutMapping("/{id}")
   @Operation(summary = "Update category")
   public ResponseEntity<ApiResponse> updateCategory(
           @PathVariable("id") Long id,
           @Valid @RequestBody CategoryRequest updateRequest, HttpServletRequest request){
       Object category = categoryService.updateCategory(id, updateRequest);
       ApiResponse response = ApiResponse.builder()
               .success(1)
               .code(HttpStatus.OK.value())
               .data(category)
               .message("Category updated successfully")
               .build();
       return ResponseUtil.buildResponse(request , response, 0L);
   }

   @DeleteMapping("/{id}")
   @Operation(summary = "Delete category")
   public ResponseEntity<ApiResponse> deleteCategory(@PathVariable("id") Long id, HttpServletRequest request){
      categoryService.deleteCategory(id);
       ApiResponse response = ApiResponse.builder()
               .success(1)
               .code(HttpStatus.OK.value())
               .message("Category deleted successfully")
               .build();
       return ResponseUtil.buildResponse(request , response, 0L);
   }



}
