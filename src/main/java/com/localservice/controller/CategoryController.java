package com.localservice.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import com.localservice.constants.AppConstants;
import com.localservice.dto.CategoryRequest;
import com.localservice.dto.CategoryResponse;
import com.localservice.service.CategoryService;
import com.localservice.util.ApiResponse;
import java.util.List;

/**
 * REST Controller for Category endpoints.
 */
@RestController
@RequestMapping("/api/v1/categories")
@Validated
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    /**
     * Create a new category.
     * POST /api/v1/categories
     */
    @PostMapping
    public ResponseEntity<ApiResponse<CategoryResponse>> createCategory(
            @Valid @RequestBody CategoryRequest request) {

        CategoryResponse response = categoryService.createCategory(request);

        return new ResponseEntity<>(
                new ApiResponse<>(
                        AppConstants.API_SUCCESS,
                        AppConstants.CATEGORY_CREATED_SUCCESSFULLY,
                        response
                ),
                HttpStatus.CREATED
        );
    }

    /**
     * Update an existing category.
     * PUT /api/v1/categories/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CategoryResponse>> updateCategory(
            @PathVariable @Min(1) Long id,
            @Valid @RequestBody CategoryRequest request) {

        CategoryResponse response = categoryService.updateCategory(id, request);

        return new ResponseEntity<>(
                new ApiResponse<>(
                        AppConstants.API_SUCCESS,
                        AppConstants.CATEGORY_UPDATED_SUCCESSFULLY,
                        response
                ),
                HttpStatus.OK
        );
    }

    /**
     * Delete a category.
     * DELETE /api/v1/categories/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> deleteCategory(
            @PathVariable @Min(1) Long id) {

        categoryService.deleteCategory(id);

        return new ResponseEntity<>(
                new ApiResponse<>(
                        AppConstants.API_SUCCESS,
                        AppConstants.CATEGORY_DELETED_SUCCESSFULLY
                ),
                HttpStatus.OK
        );
    }

    /**
     * Get category by id.
     * GET /api/v1/categories/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CategoryResponse>> getCategoryById(
            @PathVariable @Min(1) Long id) {

        CategoryResponse response = categoryService.getCategoryById(id);

        return new ResponseEntity<>(
                new ApiResponse<>(
                        AppConstants.API_SUCCESS,
                        "Category retrieved successfully",
                        response
                ),
                HttpStatus.OK
        );
    }

    /**
     * Get all categories with pagination.
     * GET /api/v1/categories?page=0&size=10&sort=categoryName,asc
     */
    @GetMapping
    public ResponseEntity<ApiResponse<Page<CategoryResponse>>> getAllCategories(
            @PageableDefault(size = 10, page = 0, sort = "id", direction = Sort.Direction.ASC)
            Pageable pageable) {

        Page<CategoryResponse> response = categoryService.getAllCategories(pageable);

        return new ResponseEntity<>(
                new ApiResponse<>(
                        AppConstants.API_SUCCESS,
                        "Categories retrieved successfully",
                        response
                ),
                HttpStatus.OK
        );
    }

    /**
     * Get all active categories.
     * GET /api/v1/categories/active/list
     */
    @GetMapping("/active/list")
    public ResponseEntity<ApiResponse<List<CategoryResponse>>> getAllActiveCategories() {

        List<CategoryResponse> response = categoryService.getAllActiveCategories();

        return new ResponseEntity<>(
                new ApiResponse<>(
                        AppConstants.API_SUCCESS,
                        "Active categories retrieved successfully",
                        response
                ),
                HttpStatus.OK
        );
    }
}
