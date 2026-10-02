package com.localservice.service;

import com.localservice.dto.ServiceProviderRequest;
import com.localservice.dto.ServiceProviderResponse;
import com.localservice.util.PaginationResponse;
import org.springframework.data.domain.Pageable;

/**
 * Service interface for ServiceProvider operations.
 */
public interface ServiceProviderService {

    /**
     * Create a new service provider.
     * @param request the service provider request
     * @return the created service provider response
     */
    ServiceProviderResponse createServiceProvider(ServiceProviderRequest request);

    /**
     * Update an existing service provider.
     * @param id the service provider id
     * @param request the service provider request
     * @return the updated service provider response
     */
    ServiceProviderResponse updateServiceProvider(Long id, ServiceProviderRequest request);

    /**
     * Delete a service provider by id.
     * @param id the service provider id
     */
    void deleteServiceProvider(Long id);

    /**
     * Get service provider by id.
     * @param id the service provider id
     * @return the service provider response
     */
    ServiceProviderResponse getServiceProviderById(Long id);

    /**
     * Get all service providers with pagination.
     * @param pageable the pagination information
     * @return pagination response of service provider responses
     */
    PaginationResponse<ServiceProviderResponse> getAllServiceProviders(Pageable pageable);

    /**
     * Search service providers by category.
     * @param categoryId the category id
     * @param pageable the pagination information
     * @return pagination response of service provider responses
     */
    PaginationResponse<ServiceProviderResponse> searchByCategory(Long categoryId, Pageable pageable);

    /**
     * Search service providers by city.
     * @param city the city
     * @param pageable the pagination information
     * @return pagination response of service provider responses
     */
    PaginationResponse<ServiceProviderResponse> searchByCity(String city, Pageable pageable);

    /**
     * Search service providers by locality.
     * @param locality the locality
     * @param pageable the pagination information
     * @return pagination response of service provider responses
     */
    PaginationResponse<ServiceProviderResponse> searchByLocality(String locality, Pageable pageable);

    /**
     * Search service providers by category and city.
     * @param categoryId the category id
     * @param city the city
     * @param pageable the pagination information
     * @return pagination response of service provider responses
     */
    PaginationResponse<ServiceProviderResponse> searchByCategoryAndCity(Long categoryId, String city, Pageable pageable);

    /**
     * Search service providers by category and locality.
     * @param categoryId the category id
     * @param locality the locality
     * @param pageable the pagination information
     * @return pagination response of service provider responses
     */
    PaginationResponse<ServiceProviderResponse> searchByCategoryAndLocality(Long categoryId, String locality, Pageable pageable);
}
