package com.localservice.mapper;

import org.springframework.stereotype.Component;
import com.localservice.dto.CategoryRequest;
import com.localservice.dto.CategoryResponse;
import com.localservice.entity.Category;

/**
 * Mapper for converting between Category Entity and DTOs.
 */
@Component
public class CategoryMapper {

    /**
     * Convert CategoryRequest to Category Entity.
     * @param request the category request DTO
     * @return the category entity
     */
    public Category requestToEntity(CategoryRequest request) {
        if (request == null) {
            return null;
        }

        Category category = new Category();
        category.setCategoryName(request.getCategoryName());
        category.setDescription(request.getDescription());
        category.setActive(request.getActive());

        return category;
    }

    /**
     * Convert Category Entity to CategoryResponse.
     * @param category the category entity
     * @return the category response DTO
     */
    public CategoryResponse entityToResponse(Category category) {
        if (category == null) {
            return null;
        }

        CategoryResponse response = new CategoryResponse();
        response.setId(category.getId());
        response.setCategoryName(category.getCategoryName());
        response.setDescription(category.getDescription());
        response.setActive(category.getActive());
        response.setCreatedAt(category.getCreatedAt());
        response.setUpdatedAt(category.getUpdatedAt());

        return response;
    }

    /**
     * Update Category entity from CategoryRequest.
     * @param request the category request DTO
     * @param category the category entity to update
     */
    public void updateEntityFromRequest(CategoryRequest request, Category category) {
        if (request == null) {
            return;
        }

        if (request.getCategoryName() != null) {
            category.setCategoryName(request.getCategoryName());
        }
        if (request.getDescription() != null) {
            category.setDescription(request.getDescription());
        }
        if (request.getActive() != null) {
            category.setActive(request.getActive());
        }
    }
}
