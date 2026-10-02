package com.localservice.controller;

import com.localservice.dto.AdminDashboardResponse;
import com.localservice.dto.AreaRequest;
import com.localservice.dto.AreaResponse;
import com.localservice.dto.CategoryRequest;
import com.localservice.dto.CategoryResponse;
import com.localservice.entity.*;
import com.localservice.exception.ResourceNotFoundException;
import com.localservice.repository.*;
import com.localservice.util.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final ServiceProviderRepository serviceProviderRepository;
    private final CategoryRepository categoryRepository;
    private final AreaRepository areaRepository;
    private final ServiceRequestRepository serviceRequestRepository;
    private final ReviewRepository reviewRepository;

    public AdminController(ServiceProviderRepository serviceProviderRepository,
                          CategoryRepository categoryRepository,
                          AreaRepository areaRepository,
                          ServiceRequestRepository serviceRequestRepository,
                          ReviewRepository reviewRepository) {
        this.serviceProviderRepository = serviceProviderRepository;
        this.categoryRepository = categoryRepository;
        this.areaRepository = areaRepository;
        this.serviceRequestRepository = serviceRequestRepository;
        this.reviewRepository = reviewRepository;
    }

    @GetMapping("/providers")
    public ResponseEntity<ApiResponse<List<ServiceProvider>>> getAllProviders() {
        return ResponseEntity.ok(new ApiResponse<>("success", "Providers retrieved successfully", serviceProviderRepository.findAll()));
    }

    @PatchMapping("/providers/{id}/approve")
    public ResponseEntity<ApiResponse<Object>> approveProvider(@PathVariable Long id) {
        ServiceProvider provider = serviceProviderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Provider not found"));
        provider.setApprovalStatus("APPROVED");
        provider.setActive(true);
        serviceProviderRepository.save(provider);
        return ResponseEntity.ok(new ApiResponse<>("success", "Provider approved successfully", null));
    }

    @PatchMapping("/providers/{id}/reject")
    public ResponseEntity<ApiResponse<Object>> rejectProvider(@PathVariable Long id) {
        ServiceProvider provider = serviceProviderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Provider not found"));
        provider.setApprovalStatus("REJECTED");
        provider.setActive(false);
        serviceProviderRepository.save(provider);
        return ResponseEntity.ok(new ApiResponse<>("success", "Provider rejected successfully", null));
    }

    @PatchMapping("/providers/{id}/suspend")
    public ResponseEntity<ApiResponse<Object>> suspendProvider(@PathVariable Long id) {
        ServiceProvider provider = serviceProviderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Provider not found"));
        provider.setApprovalStatus("SUSPENDED");
        provider.setActive(false);
        serviceProviderRepository.save(provider);
        return ResponseEntity.ok(new ApiResponse<>("success", "Provider suspended successfully", null));
    }

    @GetMapping("/categories")
    public ResponseEntity<ApiResponse<List<CategoryResponse>>> getCategories() {
        List<CategoryResponse> response = categoryRepository.findAll().stream().map(category -> {
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

    @PostMapping("/categories")
    public ResponseEntity<ApiResponse<CategoryResponse>> createCategory(@Valid @RequestBody CategoryRequest request) {
        if (categoryRepository.findByCategoryNameIgnoreCase(request.getCategoryName()).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new ApiResponse<>("error", "Category already exists"));
        }

        Category category = new Category();
        category.setCategoryName(request.getCategoryName());
        category.setDescription(request.getDescription());
        category.setActive(request.getActive() != null ? request.getActive() : true);
        Category saved = categoryRepository.save(category);

        CategoryResponse response = new CategoryResponse();
        response.setId(saved.getId());
        response.setCategoryName(saved.getCategoryName());
        response.setDescription(saved.getDescription());
        response.setActive(saved.getActive());
        response.setCreatedAt(saved.getCreatedAt());
        response.setUpdatedAt(saved.getUpdatedAt());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>("success", "Category created successfully", response));
    }

    @GetMapping("/areas")
    public ResponseEntity<ApiResponse<List<AreaResponse>>> getAreas() {
        List<AreaResponse> response = areaRepository.findAll().stream().map(area -> {
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

    @PostMapping("/areas")
    public ResponseEntity<ApiResponse<AreaResponse>> createArea(@Valid @RequestBody AreaRequest request) {
        if (areaRepository.existsByNameIgnoreCaseAndCityIgnoreCase(request.getName(), request.getCity())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new ApiResponse<>("error", "Area already exists for this city"));
        }

        Area area = new Area();
        area.setName(request.getName());
        area.setCity(request.getCity());
        area.setState(request.getState());
        area.setPincode(request.getPincode());
        area.setActive(request.getActive() != null ? request.getActive() : true);
        Area saved = areaRepository.save(area);

        AreaResponse response = new AreaResponse();
        response.setId(saved.getId());
        response.setName(saved.getName());
        response.setCity(saved.getCity());
        response.setState(saved.getState());
        response.setPincode(saved.getPincode());
        response.setActive(saved.getActive());
        response.setCreatedAt(saved.getCreatedAt());
        response.setUpdatedAt(saved.getUpdatedAt());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>("success", "Area created successfully", response));
    }

    @GetMapping("/service-requests")
    public ResponseEntity<ApiResponse<List<ServiceRequest>>> getServiceRequests() {
        return ResponseEntity.ok(new ApiResponse<>("success", "Service requests retrieved successfully", serviceRequestRepository.findAll()));
    }

    @GetMapping("/dashboard")
    public ResponseEntity<ApiResponse<AdminDashboardResponse>> getDashboard() {
        AdminDashboardResponse dto = new AdminDashboardResponse();
        dto.setTotalProviders(serviceProviderRepository.count());
        dto.setPendingProviders(serviceProviderRepository.findAll().stream().filter(p -> "PENDING".equalsIgnoreCase(p.getApprovalStatus())).count());
        dto.setApprovedProviders(serviceProviderRepository.findAll().stream().filter(p -> "APPROVED".equalsIgnoreCase(p.getApprovalStatus())).count());
        dto.setTotalRequests(serviceRequestRepository.count());
        dto.setPendingRequests(serviceRequestRepository.findAll().stream().filter(r -> "PENDING".equalsIgnoreCase(r.getStatus())).count());
        dto.setCompletedRequests(serviceRequestRepository.findAll().stream().filter(r -> "COMPLETED".equalsIgnoreCase(r.getStatus())).count());
        dto.setTotalCategories(categoryRepository.count());
        dto.setTotalAreas(areaRepository.count());
        return ResponseEntity.ok(new ApiResponse<>("success", "Dashboard retrieved successfully", dto));
    }
}
