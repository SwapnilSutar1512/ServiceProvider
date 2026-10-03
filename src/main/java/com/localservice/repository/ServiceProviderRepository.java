package com.localservice.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.localservice.entity.ServiceProvider;

/**
 * Repository interface for ServiceProvider entity.
 */
@Repository
public interface ServiceProviderRepository extends JpaRepository<ServiceProvider, Long> {

    /**
     * Find service provider by email.
     * @param email the email
     * @return Optional containing the service provider if found
     */
    Optional<ServiceProvider> findByEmailIgnoreCase(String email);

    /**
     * Find service providers by category id.
     * @param categoryId the category id
     * @param pageable pagination information
     * @return page of service providers
     */
    Page<ServiceProvider> findByCategoryId(Long categoryId, Pageable pageable);

    /**
     * Find service providers by city.
     * @param city the city
     * @param pageable pagination information
     * @return page of service providers
     */
    Page<ServiceProvider> findByCityIgnoreCase(String city, Pageable pageable);

    /**
     * Find service providers by locality.
     * @param locality the locality
     * @param pageable pagination information
     * @return page of service providers
     */
    Page<ServiceProvider> findByLocalityIgnoreCase(String locality, Pageable pageable);

    /**
     * Find service providers by category and city.
     * @param categoryId the category id
     * @param city the city
     * @param pageable pagination information
     * @return page of service providers
     */
    Page<ServiceProvider> findByCategoryIdAndCityIgnoreCase(Long categoryId, String city, Pageable pageable);

    /**
     * Find service providers by category and locality.
     * @param categoryId the category id
     * @param locality the locality
     * @param pageable pagination information
     * @return page of service providers
     */
    Page<ServiceProvider> findByCategoryIdAndLocalityIgnoreCase(Long categoryId, String locality, Pageable pageable);

    /**
     * Find all active service providers.
     * @param pageable pagination information
     * @return page of active service providers
     */
    Page<ServiceProvider> findByActiveTrue(Pageable pageable);

    /**
     * Find active service providers by category.
     * @param categoryId the category id
     * @param pageable pagination information
     * @return page of active service providers
     */
    Page<ServiceProvider> findByActiveTrueAndCategoryId(Long categoryId, Pageable pageable);

    /**
     * Find active service providers by city.
     * @param city the city
     * @param pageable pagination information
     * @return page of active service providers
     */
    Page<ServiceProvider> findByActiveTrueAndCityIgnoreCase(String city, Pageable pageable);

    /**
     * Find active service providers by locality.
     * @param locality the locality
     * @param pageable pagination information
     * @return page of active service providers
     */
    Page<ServiceProvider> findByActiveTrueAndLocalityIgnoreCase(String locality, Pageable pageable);

    /**
     * Find active service providers by category and city.
     * @param categoryId the category id
     * @param city the city
     * @param pageable pagination information
     * @return page of active service providers
     */
    Page<ServiceProvider> findByActiveTrueAndCategoryIdAndCityIgnoreCase(
            Long categoryId, String city, Pageable pageable);

    /**
     * Find active service providers by category and locality.
     * @param categoryId the category id
     * @param locality the locality
     * @param pageable pagination information
     * @return page of active service providers
     */
    Page<ServiceProvider> findByActiveTrueAndCategoryIdAndLocalityIgnoreCase(
            Long categoryId, String locality, Pageable pageable);

    Page<ServiceProvider> findByActiveTrueAndApprovalStatus(
            String approvalStatus,
            Pageable pageable);
    Page<ServiceProvider> findByActiveTrueAndApprovalStatusAndCategoryId(
            String approvalStatus,
            Long categoryId,
            Pageable pageable);
    Page<ServiceProvider> findByActiveTrueAndApprovalStatusAndCityIgnoreCase(
            String approvalStatus,
            String city,
            Pageable pageable);
    Page<ServiceProvider> findByActiveTrueAndApprovalStatusAndLocalityIgnoreCase(
            String approvalStatus,
            String locality,
            Pageable pageable);
    Page<ServiceProvider> findByActiveTrueAndApprovalStatusAndCategoryIdAndCityIgnoreCase(
            String approvalStatus,
            Long categoryId,
            String city,
            Pageable pageable);

    @Query(value = "SELECT sp.* FROM service_providers sp " +
            "WHERE sp.active = true AND sp.approval_status = :approvalStatus " +
            "AND (:categoryId IS NULL OR sp.category_id = :categoryId) " +
            "AND (:city IS NULL OR LOWER(sp.city) = LOWER(:city)) " +
            "AND (:locality IS NULL OR LOWER(sp.locality) = LOWER(:locality)) " +
            "ORDER BY CASE WHEN sp.latitude IS NULL OR sp.longitude IS NULL THEN 1 ELSE 0 END, " +
            "CASE WHEN sp.latitude IS NULL OR sp.longitude IS NULL THEN 0 ELSE " +
            "6371 * 2 * ASIN(SQRT(POWER(SIN(RADIANS(sp.latitude - :latitude) / 2), 2) + " +
            "COS(RADIANS(:latitude)) * COS(RADIANS(sp.latitude)) * " +
            "POWER(SIN(RADIANS(sp.longitude - :longitude) / 2), 2))) END, sp.id",
            countQuery = "SELECT COUNT(*) FROM service_providers sp " +
                    "WHERE sp.active = true AND sp.approval_status = :approvalStatus " +
                    "AND (:categoryId IS NULL OR sp.category_id = :categoryId) " +
                    "AND (:city IS NULL OR LOWER(sp.city) = LOWER(:city)) " +
                    "AND (:locality IS NULL OR LOWER(sp.locality) = LOWER(:locality))",
            nativeQuery = true)
    Page<ServiceProvider> findNearbyApprovedProviders(
            @Param("approvalStatus") String approvalStatus,
            @Param("categoryId") Long categoryId,
            @Param("city") String city,
            @Param("locality") String locality,
            @Param("latitude") Double latitude,
            @Param("longitude") Double longitude,
            Pageable pageable);
    
    /**
     * Find all service providers (non-paginated).
     * @return list of all service providers
     */
    List<ServiceProvider> findAll();

    /**
     * Find service providers by category (non-paginated).
     * @param categoryId the category id
     * @return list of service providers
     */
    List<ServiceProvider> findByCategoryId(Long categoryId);

    /**
     * Count service providers by category.
     * @param categoryId the category id
     * @return count of service providers
     */
    Long countByCategoryId(Long categoryId);
}
