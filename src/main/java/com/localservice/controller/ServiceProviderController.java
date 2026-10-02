package com.localservice.controller;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import com.localservice.constants.AppConstants;
import com.localservice.dto.ServiceProviderRequest;
import com.localservice.dto.ServiceProviderResponse;
import com.localservice.service.ServiceProviderService;
import com.localservice.util.ApiResponse;
import com.localservice.util.PaginationResponse;

/**
 * REST Controller for ServiceProvider endpoints.
 */
@RestController
@RequestMapping("/api/v1/providers")
@Validated
public class ServiceProviderController {

    private final ServiceProviderService serviceProviderService;

    public ServiceProviderController(ServiceProviderService serviceProviderService) {
        this.serviceProviderService = serviceProviderService;
    }

    /**
     * Create a new service provider.
     * POST /api/v1/providers
     */
    @PostMapping
    public ResponseEntity<ApiResponse<ServiceProviderResponse>> createServiceProvider(
            @Valid @RequestBody ServiceProviderRequest request) {

        ServiceProviderResponse response = serviceProviderService.createServiceProvider(request);

        return new ResponseEntity<>(
                new ApiResponse<>(
                        AppConstants.API_SUCCESS,
                        AppConstants.PROVIDER_CREATED_SUCCESSFULLY,
                        response
                ),
                HttpStatus.CREATED
        );
    }

    /**
     * Update an existing service provider.
     * PUT /api/v1/providers/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ServiceProviderResponse>> updateServiceProvider(
            @PathVariable @Min(1) Long id,
            @Valid @RequestBody ServiceProviderRequest request) {

        ServiceProviderResponse response = serviceProviderService.updateServiceProvider(id, request);

        return new ResponseEntity<>(
                new ApiResponse<>(
                        AppConstants.API_SUCCESS,
                        AppConstants.PROVIDER_UPDATED_SUCCESSFULLY,
                        response
                ),
                HttpStatus.OK
        );
    }

    /**
     * Delete a service provider.
     * DELETE /api/v1/providers/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> deleteServiceProvider(
            @PathVariable @Min(1) Long id) {

        serviceProviderService.deleteServiceProvider(id);

        return new ResponseEntity<>(
                new ApiResponse<>(
                        AppConstants.API_SUCCESS,
                        AppConstants.PROVIDER_DELETED_SUCCESSFULLY
                ),
                HttpStatus.OK
        );
    }

    /**
     * Get service provider by id.
     * GET /api/v1/providers/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ServiceProviderResponse>> getServiceProviderById(
            @PathVariable @Min(1) Long id) {

        ServiceProviderResponse response = serviceProviderService.getServiceProviderById(id);

        return new ResponseEntity<>(
                new ApiResponse<>(
                        AppConstants.API_SUCCESS,
                        "Service provider retrieved successfully",
                        response
                ),
                HttpStatus.OK
        );
    }

    /**
     * Get all active service providers with pagination.
     * GET /api/v1/providers?page=0&size=10&sort=id,asc
     */
    @GetMapping
    public ResponseEntity<ApiResponse<PaginationResponse<ServiceProviderResponse>>> getAllServiceProviders(
            @PageableDefault(size = 10, page = 0, sort = "id", direction = Sort.Direction.ASC)
            Pageable pageable) {

        PaginationResponse<ServiceProviderResponse> response = serviceProviderService.getAllServiceProviders(pageable);

        return new ResponseEntity<>(
                new ApiResponse<>(
                        AppConstants.API_SUCCESS,
                        "Service providers retrieved successfully",
                        response
                ),
                HttpStatus.OK
        );
    }

    /**
     * Search service providers by category.
     * GET /api/v1/providers/search/category/{categoryId}
     */
    @GetMapping("/search/category/{categoryId}")
    public ResponseEntity<ApiResponse<PaginationResponse<ServiceProviderResponse>>> searchByCategory(
            @PathVariable @Min(1) Long categoryId,
            @PageableDefault(size = 10, page = 0, sort = "id", direction = Sort.Direction.ASC)
            Pageable pageable) {

        PaginationResponse<ServiceProviderResponse> response = serviceProviderService.searchByCategory(categoryId, pageable);

        return new ResponseEntity<>(
                new ApiResponse<>(
                        AppConstants.API_SUCCESS,
                        "Service providers retrieved by category",
                        response
                ),
                HttpStatus.OK
        );
    }

    /**
     * Search service providers by city.
     * GET /api/v1/providers/search/city/{city}
     */
    @GetMapping("/search/city/{city}")
    public ResponseEntity<ApiResponse<PaginationResponse<ServiceProviderResponse>>> searchByCity(
            @PathVariable @NotBlank String city,
            @PageableDefault(size = 10, page = 0, sort = "id", direction = Sort.Direction.ASC)
            Pageable pageable) {

        PaginationResponse<ServiceProviderResponse> response = serviceProviderService.searchByCity(city, pageable);

        return new ResponseEntity<>(
                new ApiResponse<>(
                        AppConstants.API_SUCCESS,
                        "Service providers retrieved by city",
                        response
                ),
                HttpStatus.OK
        );
    }

    /**
     * Search service providers by locality.
     * GET /api/v1/providers/search/locality/{locality}
     */
    @GetMapping("/search/locality/{locality}")
    public ResponseEntity<ApiResponse<PaginationResponse<ServiceProviderResponse>>> searchByLocality(
            @PathVariable @NotBlank String locality,
            @PageableDefault(size = 10, page = 0, sort = "id", direction = Sort.Direction.ASC)
            Pageable pageable) {

        PaginationResponse<ServiceProviderResponse> response = serviceProviderService.searchByLocality(locality, pageable);

        return new ResponseEntity<>(
                new ApiResponse<>(
                        AppConstants.API_SUCCESS,
                        "Service providers retrieved by locality",
                        response
                ),
                HttpStatus.OK
        );
    }

    /**
     * Search service providers by category and city.
     * GET /api/v1/providers/search/category/{categoryId}/city/{city}
     */
    @GetMapping("/search/category/{categoryId}/city/{city}")
    public ResponseEntity<ApiResponse<PaginationResponse<ServiceProviderResponse>>> searchByCategoryAndCity(
            @PathVariable @Min(1) Long categoryId,
            @PathVariable @NotBlank String city,
            @PageableDefault(size = 10, page = 0, sort = "id", direction = Sort.Direction.ASC)
            Pageable pageable) {

        PaginationResponse<ServiceProviderResponse> response = serviceProviderService
                .searchByCategoryAndCity(categoryId, city, pageable);

        return new ResponseEntity<>(
                new ApiResponse<>(
                        AppConstants.API_SUCCESS,
                        "Service providers retrieved by category and city",
                        response
                ),
                HttpStatus.OK
        );
    }

    /**
     * Search service providers by category and locality.
     * GET /api/v1/providers/search/category/{categoryId}/locality/{locality}
     */
    @GetMapping("/search/category/{categoryId}/locality/{locality}")
    public ResponseEntity<ApiResponse<PaginationResponse<ServiceProviderResponse>>> searchByCategoryAndLocality(
            @PathVariable @Min(1) Long categoryId,
            @PathVariable @NotBlank String locality,
            @PageableDefault(size = 10, page = 0, sort = "id", direction = Sort.Direction.ASC)
            Pageable pageable) {

        PaginationResponse<ServiceProviderResponse> response = serviceProviderService
                .searchByCategoryAndLocality(categoryId, locality, pageable);

        return new ResponseEntity<>(
                new ApiResponse<>(
                        AppConstants.API_SUCCESS,
                        "Service providers retrieved by category and locality",
                        response
                ),
                HttpStatus.OK
        );
    }
}
