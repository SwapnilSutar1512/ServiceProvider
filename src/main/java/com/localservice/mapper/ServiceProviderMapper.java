package com.localservice.mapper;

import org.springframework.stereotype.Component;
import com.localservice.dto.ServiceProviderRequest;
import com.localservice.dto.ServiceProviderResponse;
import com.localservice.entity.ServiceProvider;

/**
 * Mapper for converting between ServiceProvider Entity and DTOs.
 */
@Component
public class ServiceProviderMapper {

    /**
     * Convert ServiceProviderRequest to ServiceProvider Entity.
     * @param request the service provider request DTO
     * @return the service provider entity
     */
    public ServiceProvider requestToEntity(ServiceProviderRequest request) {
        if (request == null) {
            return null;
        }

        ServiceProvider provider = new ServiceProvider();
        provider.setFullName(request.getFullName());
        provider.setBusinessName(request.getBusinessName());
        provider.setPhoneNumber(request.getPhoneNumber());
        provider.setEmail(request.getEmail());
        provider.setExperience(request.getExperience());
        provider.setStreet(request.getStreet());
        provider.setLocality(request.getLocality());
        provider.setCity(request.getCity());
        provider.setState(request.getState());
        provider.setPincode(request.getPincode());
        provider.setLatitude(request.getLatitude());
        provider.setLongitude(request.getLongitude());
        provider.setWorkingHours(request.getWorkingHours());
        provider.setActive(request.getActive());

        return provider;
    }

    /**
     * Convert ServiceProvider Entity to ServiceProviderResponse.
     * @param provider the service provider entity
     * @return the service provider response DTO
     */
    public ServiceProviderResponse entityToResponse(ServiceProvider provider) {
        if (provider == null) {
            return null;
        }

        ServiceProviderResponse response = new ServiceProviderResponse();
        response.setId(provider.getId());
        response.setFullName(provider.getFullName());
        response.setBusinessName(provider.getBusinessName());
        response.setPhoneNumber(provider.getPhoneNumber());
        response.setEmail(provider.getEmail());
        response.setExperience(provider.getExperience());
        response.setStreet(provider.getStreet());
        response.setLocality(provider.getLocality());
        response.setCity(provider.getCity());
        response.setState(provider.getState());
        response.setPincode(provider.getPincode());
        response.setLatitude(provider.getLatitude());
        response.setLongitude(provider.getLongitude());
        response.setWorkingHours(provider.getWorkingHours());
        response.setRating(provider.getRating());
        response.setActive(provider.getActive());
        response.setCategoryId(provider.getCategory().getId());
        response.setCategoryName(provider.getCategory().getCategoryName());
        response.setCreatedAt(provider.getCreatedAt());
        response.setUpdatedAt(provider.getUpdatedAt());

        return response;
    }

    /**
     * Update ServiceProvider entity from ServiceProviderRequest.
     * @param request the service provider request DTO
     * @param provider the service provider entity to update
     */
    public void updateEntityFromRequest(ServiceProviderRequest request, ServiceProvider provider) {
        if (request == null) {
            return;
        }

        if (request.getFullName() != null) {
            provider.setFullName(request.getFullName());
        }
        if (request.getBusinessName() != null) {
            provider.setBusinessName(request.getBusinessName());
        }
        if (request.getPhoneNumber() != null) {
            provider.setPhoneNumber(request.getPhoneNumber());
        }
        if (request.getExperience() != null) {
            provider.setExperience(request.getExperience());
        }
        if (request.getStreet() != null) {
            provider.setStreet(request.getStreet());
        }
        if (request.getLocality() != null) {
            provider.setLocality(request.getLocality());
        }
        if (request.getCity() != null) {
            provider.setCity(request.getCity());
        }
        if (request.getState() != null) {
            provider.setState(request.getState());
        }
        if (request.getPincode() != null) {
            provider.setPincode(request.getPincode());
        }
        if (request.getLatitude() != null) {
            provider.setLatitude(request.getLatitude());
        }
        if (request.getLongitude() != null) {
            provider.setLongitude(request.getLongitude());
        }
        if (request.getWorkingHours() != null) {
            provider.setWorkingHours(request.getWorkingHours());
        }
        if (request.getActive() != null) {
            provider.setActive(request.getActive());
        }
    }
}
