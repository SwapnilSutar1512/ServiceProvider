package com.localservice.service;

import com.localservice.dto.CategoryRequest;
import com.localservice.dto.CategoryResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

/**
 * Service interface for Category operations.
 */
public interface CategoryService {

    /**
     * Create a new category.
     * @param request the category request
     * @return the created category response
     */
    CategoryResponse createCategory(CategoryRequest request);

    /**
     * Update an existing category.
     * @param id the category id
     * @param request the category request
     * @return the updated category response
     */
    CategoryResponse updateCategory(Long id, CategoryRequest request);

    /**
     * Delete a category by id.
     * @param id the category id
     */
    void deleteCategory(Long id);

    /**
     * Get category by id.
     * @param id the category id
     * @return the category response
     */
    CategoryResponse getCategoryById(Long id);

    /**
     * Get all categories with pagination.
     * @param pageable the pagination information
     * @return page of category responses
     */
    Page<CategoryResponse> getAllCategories(Pageable pageable);

    /**
     * Get all active categories.
     * @return list of active category responses
     */
    List<CategoryResponse> getAllActiveCategories();
}
