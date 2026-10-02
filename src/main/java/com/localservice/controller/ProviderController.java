package com.localservice.controller;

import com.localservice.dto.ServiceRequestResponse;
import com.localservice.entity.ServiceProvider;
import com.localservice.entity.ServiceRequest;
import com.localservice.entity.User;
import com.localservice.exception.ResourceNotFoundException;
import com.localservice.repository.ServiceProviderRepository;
import com.localservice.repository.ServiceRequestRepository;
import com.localservice.repository.UserRepository;
import com.localservice.util.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/provider")
public class ProviderController {

    private final ServiceRequestRepository serviceRequestRepository;
    private final ServiceProviderRepository serviceProviderRepository;
    private final UserRepository userRepository;

    public ProviderController(ServiceRequestRepository serviceRequestRepository,
                             ServiceProviderRepository serviceProviderRepository,
                             UserRepository userRepository) {
        this.serviceRequestRepository = serviceRequestRepository;
        this.serviceProviderRepository = serviceProviderRepository;
        this.userRepository = userRepository;
    }

    @GetMapping("/service-requests")
    public ResponseEntity<ApiResponse<List<ServiceRequestResponse>>> getRequests() {
        ServiceProvider provider = getCurrentProvider();
        List<ServiceRequestResponse> items = serviceRequestRepository.findByProvider(provider, org.springframework.data.domain.Pageable.unpaged())
                .getContent().stream().map(this::toResponse).toList();
        return ResponseEntity.ok(new ApiResponse<>("success", "Provider requests retrieved successfully", items));
    }

    @PatchMapping("/service-requests/{requestId}/accept")
    public ResponseEntity<ApiResponse<Object>> acceptRequest(@PathVariable String requestId) {
        ServiceProvider provider = getCurrentProvider();
        ServiceRequest request = serviceRequestRepository.findByRequestId(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Request not found"));
        if (!request.getProvider().getId().equals(provider.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ApiResponse<>("error", "You can only access your own requests"));
        }
        request.setStatus("ACCEPTED");
        serviceRequestRepository.save(request);
        return ResponseEntity.ok(new ApiResponse<>("success", "Request accepted successfully", null));
    }

    @PatchMapping("/service-requests/{requestId}/reject")
    public ResponseEntity<ApiResponse<Object>> rejectRequest(@PathVariable String requestId,
                                                             @RequestBody(required = false) java.util.Map<String, String> body) {
        ServiceProvider provider = getCurrentProvider();
        ServiceRequest request = serviceRequestRepository.findByRequestId(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Request not found"));
        if (!request.getProvider().getId().equals(provider.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ApiResponse<>("error", "You can only access your own requests"));
        }
        request.setStatus("REJECTED");
        if (body != null && body.get("reason") != null) {
            request.setRejectReason(body.get("reason"));
        }
        serviceRequestRepository.save(request);
        return ResponseEntity.ok(new ApiResponse<>("success", "Request rejected successfully", null));
    }

    @PatchMapping("/service-requests/{requestId}/complete")
    public ResponseEntity<ApiResponse<Object>> completeRequest(@PathVariable String requestId) {
        ServiceProvider provider = getCurrentProvider();
        ServiceRequest request = serviceRequestRepository.findByRequestId(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Request not found"));
        if (!request.getProvider().getId().equals(provider.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ApiResponse<>("error", "You can only access your own requests"));
        }
        if (!"ACCEPTED".equalsIgnoreCase(request.getStatus()) && !"IN_PROGRESS".equalsIgnoreCase(request.getStatus())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>("error", "Only accepted or in-progress requests can be completed"));
        }
        request.setStatus("COMPLETED");
        serviceRequestRepository.save(request);
        return ResponseEntity.ok(new ApiResponse<>("success", "Request marked as completed", null));
    }

    private ServiceProvider getCurrentProvider() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Authenticated provider not found"));
        return serviceProviderRepository.findByEmailIgnoreCase(user.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("Provider profile not found"));
    }

    private ServiceRequestResponse toResponse(ServiceRequest entity) {
        ServiceRequestResponse dto = new ServiceRequestResponse();
        dto.setId(entity.getId());
        dto.setRequestId(entity.getRequestId());
        dto.setProviderId(entity.getProvider().getId());
        dto.setProviderName(entity.getProvider().getBusinessName());
        dto.setCategoryId(entity.getCategory().getId());
        dto.setCategoryName(entity.getCategory().getCategoryName());
        dto.setAreaId(entity.getArea().getId());
        dto.setAreaName(entity.getArea().getName());
        dto.setCustomerName(entity.getCustomerName());
        dto.setCustomerMobile(entity.getCustomerMobile());
        dto.setDescription(entity.getDescription());
        dto.setPreferredDate(entity.getPreferredDate());
        dto.setPreferredTime(entity.getPreferredTime());
        dto.setStatus(entity.getStatus());
        dto.setRejectReason(entity.getRejectReason());
        dto.setCreatedAt(entity.getCreatedAt());
        return dto;
    }
}
