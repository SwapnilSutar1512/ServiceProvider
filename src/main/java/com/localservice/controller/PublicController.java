package com.localservice.controller;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.localservice.dto.AreaResponse;
import com.localservice.dto.CategoryResponse;
import com.localservice.dto.ComplaintRequest;
import com.localservice.dto.ProviderProfileResponse;
import com.localservice.dto.PublicProviderResponse;
import com.localservice.dto.PublicServiceRequestRequest;
import com.localservice.dto.ReviewRequest;
import com.localservice.entity.Area;
import com.localservice.entity.Category;
import com.localservice.entity.Complaint;
import com.localservice.entity.Review;
import com.localservice.entity.ServiceProvider;
import com.localservice.entity.ServiceRequest;
import com.localservice.exception.ResourceNotFoundException;
import com.localservice.repository.AreaRepository;
import com.localservice.repository.CategoryRepository;
import com.localservice.repository.ComplaintRepository;
import com.localservice.repository.ReviewRepository;
import com.localservice.repository.ServiceProviderRepository;
import com.localservice.repository.ServiceRequestRepository;
import com.localservice.util.ApiResponse;
import com.localservice.util.PaginationResponse;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/public")
public class PublicController {

    private final CategoryRepository categoryRepository;
    private final AreaRepository areaRepository;
    private final ServiceProviderRepository serviceProviderRepository;
    private final ServiceRequestRepository serviceRequestRepository;
    private final ReviewRepository reviewRepository;
    private final ComplaintRepository complaintRepository;

    public PublicController(CategoryRepository categoryRepository,
                           AreaRepository areaRepository,
                           ServiceProviderRepository serviceProviderRepository,
                           ServiceRequestRepository serviceRequestRepository,
                           ReviewRepository reviewRepository,
                           ComplaintRepository complaintRepository) {
        this.categoryRepository = categoryRepository;
        this.areaRepository = areaRepository;
        this.serviceProviderRepository = serviceProviderRepository;
        this.serviceRequestRepository = serviceRequestRepository;
        this.reviewRepository = reviewRepository;
        this.complaintRepository = complaintRepository;
    }

    @GetMapping("/categories")
    public ResponseEntity<ApiResponse<List<CategoryResponse>>> getCategories() {
        List<CategoryResponse> response = categoryRepository.findByActiveTrue().stream().map(category -> {
            CategoryResponse dto = new CategoryResponse();
            dto.setId(category.getId());
            dto.setCategoryName(category.getCategoryName());
            dto.setDescription(category.getDescription());
            dto.setActive(category.getActive());
            dto.setCreatedAt(category.getCreatedAt());
            dto.setUpdatedAt(category.getUpdatedAt());
            return dto;
        }).toList();
        return ResponseEntity.ok(new ApiResponse<>("success", "Categories retrieved successfully", response));
    }

    @GetMapping("/areas")
    public ResponseEntity<ApiResponse<List<AreaResponse>>> getAreas(@RequestParam(required = false) String city) {
        List<Area> areas = (city == null || city.isBlank())
                ? areaRepository.findByActiveTrueOrderByCityAscNameAsc()
                : areaRepository.findByCityIgnoreCaseAndActiveTrueOrderByNameAsc(city);

        List<AreaResponse> response = areas.stream().map(area -> {
            AreaResponse dto = new AreaResponse();
            dto.setId(area.getId());
            dto.setName(area.getName());
            dto.setCity(area.getCity());
            dto.setState(area.getState());
            dto.setPincode(area.getPincode());
            dto.setActive(area.getActive());
            dto.setCreatedAt(area.getCreatedAt());
            dto.setUpdatedAt(area.getUpdatedAt());
            return dto;
        }).toList();

        return ResponseEntity.ok(new ApiResponse<>("success", "Areas retrieved successfully", response));
    }

    @GetMapping("/providers")
    public ResponseEntity<ApiResponse<PaginationResponse<PublicProviderResponse>>> getProviders(
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String locality,
            @RequestParam(required = false) Double latitude,
            @RequestParam(required = false) Double longitude,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        if ((latitude == null) != (longitude == null)) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>("error", "Latitude and longitude must be provided together"));
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "id"));
        Page<ServiceProvider> result;

        if (latitude != null) {
            result = serviceProviderRepository.findNearbyApprovedProviders(
                    "APPROVED", categoryId, blankToNull(city), blankToNull(locality), latitude, longitude,
                    PageRequest.of(page, size));
        } else if (categoryId != null && city != null && !city.isBlank()) {

            result = serviceProviderRepository
                    .findByActiveTrueAndApprovalStatusAndCategoryIdAndCityIgnoreCase(
                            "APPROVED",
                            categoryId,
                            city,
                            pageable);

        } else if (categoryId != null) {

            result = serviceProviderRepository
                    .findByActiveTrueAndApprovalStatusAndCategoryId(
                            "APPROVED",
                            categoryId,
                            pageable);

        } else if (city != null && !city.isBlank()) {

            result = serviceProviderRepository
                    .findByActiveTrueAndApprovalStatusAndCityIgnoreCase(
                            "APPROVED",
                            city,
                            pageable);

        } else if (locality != null && !locality.isBlank()) {

            result = serviceProviderRepository
                    .findByActiveTrueAndApprovalStatusAndLocalityIgnoreCase(
                            "APPROVED",
                            locality,
                            pageable);

        } else {

            result = serviceProviderRepository
                    .findByActiveTrueAndApprovalStatus(
                            "APPROVED",
                            pageable);
        }
        List<PublicProviderResponse> content = result.getContent().stream().map(provider -> {
            PublicProviderResponse dto = new PublicProviderResponse();
            dto.setId(provider.getId());
            dto.setBusinessName(provider.getBusinessName());
            dto.setCity(provider.getCity());
            dto.setLocality(provider.getLocality());
            dto.setWorkingHours(provider.getWorkingHours());
            dto.setCategoryName(provider.getCategory().getCategoryName());
            dto.setExperience(provider.getExperience());
            dto.setRating(provider.getRating());
            dto.setPhoneNumber(provider.getPhoneNumber());
            if (latitude != null && provider.getLatitude() != null && provider.getLongitude() != null) {
                dto.setDistanceKm(distanceInKilometers(latitude, longitude,
                        provider.getLatitude(), provider.getLongitude()));
            }
            dto.setCreatedAt(provider.getCreatedAt());
            return dto;
        }).toList();

        PaginationResponse<PublicProviderResponse> payload = new PaginationResponse<>(
                content, result.getNumber(), result.getSize(), result.getTotalElements(), result.getTotalPages());
        return ResponseEntity.ok(new ApiResponse<>("success", "Providers retrieved successfully", payload));
    }

    @GetMapping("/providers/{id}")
    public ResponseEntity<ApiResponse<ProviderProfileResponse>> getProviderProfile(@PathVariable Long id) {
        ServiceProvider provider = serviceProviderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Provider not found"));

        if (!Boolean.TRUE.equals(provider.getActive()) || !"APPROVED".equalsIgnoreCase(provider.getApprovalStatus())) {
            throw new ResourceNotFoundException("Provider is not available in the marketplace");
        }

        ProviderProfileResponse dto = new ProviderProfileResponse();
        dto.setId(provider.getId());
        dto.setBusinessName(provider.getBusinessName());
        dto.setFullName(provider.getFullName());
        dto.setCategoryName(provider.getCategory().getCategoryName());
        dto.setCity(provider.getCity());
        dto.setLocality(provider.getLocality());
        dto.setPhoneNumber(provider.getPhoneNumber());
        dto.setLatitude(provider.getLatitude());
        dto.setLongitude(provider.getLongitude());
        dto.setWorkingHours(provider.getWorkingHours());
        dto.setExperience(provider.getExperience());
        dto.setRating(provider.getRating());
        dto.setApprovalStatus(provider.getApprovalStatus());
        dto.setServiceAreas(List.of(provider.getLocality(), provider.getCity()));

        return ResponseEntity.ok(new ApiResponse<>("success", "Provider profile retrieved successfully", dto));
    }

    private String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value;
    }

    private double distanceInKilometers(double latitude, double longitude,
                                        double providerLatitude, double providerLongitude) {
        double latitudeDelta = Math.toRadians(providerLatitude - latitude);
        double longitudeDelta = Math.toRadians(providerLongitude - longitude);
        double haversine = Math.pow(Math.sin(latitudeDelta / 2), 2)
                + Math.cos(Math.toRadians(latitude)) * Math.cos(Math.toRadians(providerLatitude))
                * Math.pow(Math.sin(longitudeDelta / 2), 2);
        return Math.round(6371 * 2 * Math.asin(Math.sqrt(haversine)) * 10.0) / 10.0;
    }

    @PostMapping("/service-requests")
    public ResponseEntity<ApiResponse<Object>> createServiceRequest(@Valid @RequestBody PublicServiceRequestRequest request) {
        ServiceProvider provider = serviceProviderRepository.findById(request.getProviderId())
                .orElseThrow(() -> new ResourceNotFoundException("Provider not found"));
        Category category = categoryRepository.findById(request.getServiceId())
                .orElseThrow(() -> new ResourceNotFoundException("Service category not found"));
        Area area = areaRepository.findById(request.getAreaId())
                .orElseThrow(() -> new ResourceNotFoundException("Area not found"));

        if (!Boolean.TRUE.equals(provider.getActive()) || !"APPROVED".equalsIgnoreCase(provider.getApprovalStatus())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>("error", "This provider is not currently accepting service requests"));
        }

        ServiceRequest entity = new ServiceRequest();
        entity.setProvider(provider);
        entity.setCategory(category);
        entity.setArea(area);
        entity.setCustomerName(request.getName());
        entity.setCustomerMobile(request.getMobile());
        entity.setDescription(request.getDescription());
        entity.setPreferredDate(request.getPreferredDate());
        entity.setPreferredTime(request.getPreferredTime());
        entity.setAddress(request.getAddress());
        entity.setPhotoUrl(request.getPhotoUrl());
        entity.setStatus("PENDING");
        entity.setRequestId(generateRequestId());
        ServiceRequest saved = serviceRequestRepository.save(entity);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>("success", "Service request submitted successfully", Map.of("requestId", saved.getRequestId())));
    }

    @PostMapping("/service-requests/{requestId}/review")
    public ResponseEntity<ApiResponse<Object>> submitReview(@PathVariable String requestId,
                                                          @Valid @RequestBody ReviewRequest reviewRequest) {
        ServiceRequest request = serviceRequestRepository.findByRequestId(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Service request not found"));

        if (!"COMPLETED".equalsIgnoreCase(request.getStatus())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>("error", "Review can only be submitted after completion"));
        }

        if (reviewRepository.findByServiceRequestId(request.getId()).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new ApiResponse<>("error", "A review already exists for this request"));
        }

        Review review = new Review();
        review.setProvider(request.getProvider());
        review.setServiceRequest(request);
        review.setRating(reviewRequest.getRating());
        review.setComment(reviewRequest.getComment());
        review.setApproved(false);
        reviewRepository.save(review);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>("success", "Review submitted successfully", null));
    }

    @PostMapping("/complaints")
    public ResponseEntity<ApiResponse<Object>> createComplaint(@Valid @RequestBody ComplaintRequest request) {
        ServiceRequest serviceRequest = null;
        if (request.getServiceRequestId() != null) {
            serviceRequest = serviceRequestRepository.findById(request.getServiceRequestId())
                    .orElseThrow(() -> new ResourceNotFoundException("Service request not found"));
        }

        ServiceProvider provider = null;
        if (request.getProviderId() != null) {
            provider = serviceProviderRepository.findById(request.getProviderId())
                    .orElseThrow(() -> new ResourceNotFoundException("Provider not found"));
        }

        Complaint complaint = new Complaint();
        complaint.setCustomerName(request.getCustomerName());
        complaint.setCustomerMobile(request.getCustomerMobile());
        complaint.setReason(request.getReason());
        complaint.setDescription(request.getDescription());
        complaint.setProvider(provider);
        complaint.setServiceRequest(serviceRequest);
        complaintRepository.save(complaint);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>("success", "Complaint submitted successfully", null));
    }

    private String generateRequestId() {
        String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        long count = serviceRequestRepository.count() + 1;
        return "REQ-" + today + "-" + String.format("%06d", count);
    }
}
