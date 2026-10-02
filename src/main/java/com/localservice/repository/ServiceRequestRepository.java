package com.localservice.repository;

import com.localservice.entity.ServiceProvider;
import com.localservice.entity.ServiceRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ServiceRequestRepository extends JpaRepository<ServiceRequest, Long> {
    Optional<ServiceRequest> findByRequestId(String requestId);
    Page<ServiceRequest> findByProvider(ServiceProvider provider, Pageable pageable);
    Page<ServiceRequest> findByStatus(String status, Pageable pageable);
    long count();
}
