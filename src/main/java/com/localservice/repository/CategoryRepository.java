package com.localservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.localservice.entity.Category;
import java.util.Optional;
import java.util.List;

/**
 * Repository interface for Category entity.
 */
@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    /**
     * Find category by name.
     * @param categoryName the category name
     * @return Optional containing the category if found
     */
    Optional<Category> findByCategoryNameIgnoreCase(String categoryName);

    /**
     * Find all active categories.
     * @return list of active categories
     */
    List<Category> findByActiveTrue();
}
