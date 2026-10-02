package com.localservice.repository;

import com.localservice.entity.Area;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AreaRepository extends JpaRepository<Area, Long> {
    List<Area> findByActiveTrueOrderByCityAscNameAsc();
    List<Area> findByCityIgnoreCaseAndActiveTrueOrderByNameAsc(String city);
    boolean existsByNameIgnoreCaseAndCityIgnoreCase(String name, String city);
}
