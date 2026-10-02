package com.localservice.service.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.localservice.constants.AppConstants;
import com.localservice.dto.ServiceProviderRequest;
import com.localservice.dto.ServiceProviderResponse;
import com.localservice.entity.Category;
import com.localservice.entity.ServiceProvider;
import com.localservice.exception.ResourceAlreadyExistsException;
import com.localservice.exception.ResourceNotFoundException;
import com.localservice.mapper.ServiceProviderMapper;
import com.localservice.repository.CategoryRepository;
import com.localservice.repository.ServiceProviderRepository;
import com.localservice.service.ServiceProviderService;
import com.localservice.util.PaginationResponse;
import java.util.stream.Collectors;

/**
 * Service implementation for ServiceProvider operations.
 */
@Service
@Transactional
public class ServiceProviderServiceImpl implements ServiceProviderService {

    private final ServiceProviderRepository serviceProviderRepository;
    private final CategoryRepository categoryRepository;
    private final ServiceProviderMapper serviceProviderMapper;

    public ServiceProviderServiceImpl(ServiceProviderRepository serviceProviderRepository,
                                     CategoryRepository categoryRepository,
                                     ServiceProviderMapper serviceProviderMapper) {
        this.serviceProviderRepository = serviceProviderRepository;
        this.categoryRepository = categoryRepository;
        this.serviceProviderMapper = serviceProviderMapper;
    }

    @Override
    public ServiceProviderResponse createServiceProvider(ServiceProviderRequest request) {
        // Check if email already exists
        if (serviceProviderRepository.findByEmailIgnoreCase(request.getEmail()).isPresent()) {
            throw new ResourceAlreadyExistsException("Service provider with this email already exists");
        }

        // Verify category exists
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException(AppConstants.CATEGORY_NOT_FOUND));

        ServiceProvider provider = serviceProviderMapper.requestToEntity(request);
        provider.setCategory(category);

        ServiceProvider savedProvider = serviceProviderRepository.save(provider);
        return serviceProviderMapper.entityToResponse(savedProvider);
    }

    @Override
    public ServiceProviderResponse updateServiceProvider(Long id, ServiceProviderRequest request) {
        ServiceProvider provider = serviceProviderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(AppConstants.PROVIDER_NOT_FOUND));

        // If email is being updated, check if it already exists
        if (request.getEmail() != null && !request.getEmail().equalsIgnoreCase(provider.getEmail())) {
            if (serviceProviderRepository.findByEmailIgnoreCase(request.getEmail()).isPresent()) {
                throw new ResourceAlreadyExistsException("Service provider with this email already exists");
            }
            provider.setEmail(request.getEmail());
        }

        // If category is being updated, verify it exists
        if (request.getCategoryId() != null && !request.getCategoryId().equals(provider.getCategory().getId())) {
            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException(AppConstants.CATEGORY_NOT_FOUND));
            provider.setCategory(category);
        }

        serviceProviderMapper.updateEntityFromRequest(request, provider);
        ServiceProvider updatedProvider = serviceProviderRepository.save(provider);

        return serviceProviderMapper.entityToResponse(updatedProvider);
    }

    @Override
    public void deleteServiceProvider(Long id) {
        ServiceProvider provider = serviceProviderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(AppConstants.PROVIDER_NOT_FOUND));

        serviceProviderRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public ServiceProviderResponse getServiceProviderById(Long id) {
        ServiceProvider provider = serviceProviderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(AppConstants.PROVIDER_NOT_FOUND));

        return serviceProviderMapper.entityToResponse(provider);
    }

    @Override
    @Transactional(readOnly = true)
    public PaginationResponse<ServiceProviderResponse> getAllServiceProviders(Pageable pageable) {
        Page<ServiceProvider> page = serviceProviderRepository.findByActiveTrue(pageable);

        return new PaginationResponse<>(
                page.getContent().stream()
                        .map(serviceProviderMapper::entityToResponse)
                        .collect(Collectors.toList()),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public PaginationResponse<ServiceProviderResponse> searchByCategory(Long categoryId, Pageable pageable) {
        // Verify category exists
        categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException(AppConstants.CATEGORY_NOT_FOUND));

        Page<ServiceProvider> page = serviceProviderRepository
                .findByActiveTrueAndCategoryId(categoryId, pageable);

        return new PaginationResponse<>(
                page.getContent().stream()
                        .map(serviceProviderMapper::entityToResponse)
                        .collect(Collectors.toList()),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public PaginationResponse<ServiceProviderResponse> searchByCity(String city, Pageable pageable) {
        Page<ServiceProvider> page = serviceProviderRepository
                .findByActiveTrueAndCityIgnoreCase(city, pageable);

        return new PaginationResponse<>(
                page.getContent().stream()
                        .map(serviceProviderMapper::entityToResponse)
                        .collect(Collectors.toList()),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public PaginationResponse<ServiceProviderResponse> searchByLocality(String locality, Pageable pageable) {
        Page<ServiceProvider> page = serviceProviderRepository
                .findByActiveTrueAndLocalityIgnoreCase(locality, pageable);

        return new PaginationResponse<>(
                page.getContent().stream()
                        .map(serviceProviderMapper::entityToResponse)
                        .collect(Collectors.toList()),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public PaginationResponse<ServiceProviderResponse> searchByCategoryAndCity(Long categoryId, String city, Pageable pageable) {
        // Verify category exists
        categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException(AppConstants.CATEGORY_NOT_FOUND));

        Page<ServiceProvider> page = serviceProviderRepository
                .findByActiveTrueAndCategoryIdAndCityIgnoreCase(categoryId, city, pageable);

        return new PaginationResponse<>(
                page.getContent().stream()
                        .map(serviceProviderMapper::entityToResponse)
                        .collect(Collectors.toList()),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public PaginationResponse<ServiceProviderResponse> searchByCategoryAndLocality(Long categoryId, String locality, Pageable pageable) {
        // Verify category exists
        categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException(AppConstants.CATEGORY_NOT_FOUND));

        Page<ServiceProvider> page = serviceProviderRepository
                .findByActiveTrueAndCategoryIdAndLocalityIgnoreCase(categoryId, locality, pageable);

        return new PaginationResponse<>(
                page.getContent().stream()
                        .map(serviceProviderMapper::entityToResponse)
                        .collect(Collectors.toList()),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages()
        );
    }
}
