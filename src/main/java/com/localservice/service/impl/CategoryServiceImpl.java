package com.localservice.service.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.localservice.constants.AppConstants;
import com.localservice.dto.CategoryRequest;
import com.localservice.dto.CategoryResponse;
import com.localservice.entity.Category;
import com.localservice.exception.ResourceAlreadyExistsException;
import com.localservice.exception.ResourceNotFoundException;
import com.localservice.mapper.CategoryMapper;
import com.localservice.repository.CategoryRepository;
import com.localservice.service.CategoryService;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service implementation for Category operations.
 */
@Service
@Transactional
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    public CategoryServiceImpl(CategoryRepository categoryRepository, CategoryMapper categoryMapper) {
        this.categoryRepository = categoryRepository;
        this.categoryMapper = categoryMapper;
    }

    @Override
    public CategoryResponse createCategory(CategoryRequest request) {
        // Check if category already exists
        if (categoryRepository.findByCategoryNameIgnoreCase(request.getCategoryName()).isPresent()) {
            throw new ResourceAlreadyExistsException(AppConstants.CATEGORY_ALREADY_EXISTS);
        }

        Category category = categoryMapper.requestToEntity(request);
        Category savedCategory = categoryRepository.save(category);

        return categoryMapper.entityToResponse(savedCategory);
    }

    @Override
    public CategoryResponse updateCategory(Long id, CategoryRequest request) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(AppConstants.CATEGORY_NOT_FOUND));

        // Check if new name already exists (if name is being updated)
        if (request.getCategoryName() != null &&
                !request.getCategoryName().equalsIgnoreCase(category.getCategoryName())) {
            if (categoryRepository.findByCategoryNameIgnoreCase(request.getCategoryName()).isPresent()) {
                throw new ResourceAlreadyExistsException(AppConstants.CATEGORY_ALREADY_EXISTS);
            }
        }

        categoryMapper.updateEntityFromRequest(request, category);
        Category updatedCategory = categoryRepository.save(category);

        return categoryMapper.entityToResponse(updatedCategory);
    }

    @Override
    @Transactional
    public void deleteCategory(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(AppConstants.CATEGORY_NOT_FOUND));

        categoryRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryResponse getCategoryById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(AppConstants.CATEGORY_NOT_FOUND));

        return categoryMapper.entityToResponse(category);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CategoryResponse> getAllCategories(Pageable pageable) {
        return categoryRepository.findAll(pageable)
                .map(categoryMapper::entityToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryResponse> getAllActiveCategories() {
        return categoryRepository.findByActiveTrue().stream()
                .map(categoryMapper::entityToResponse)
                .collect(Collectors.toList());
    }
}
