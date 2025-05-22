package com.tip.b18.electronicsales.repositories;

import com.tip.b18.electronicsales.entities.Brand;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.UUID;

@Repository
public interface BrandRepository extends JpaRepository<Brand, UUID> {
    @Query("SELECT b FROM Brand b WHERE b.name LIKE CONCAT('%', :search, '%')")
    Page<Brand> findByBrandName(@Param("search") String search, Pageable pageable);

    boolean existsByName(String name);
}
