package com.tip.b18.electronicsales.repositories;

import com.tip.b18.electronicsales.entities.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CategoryRepository extends JpaRepository<Category, UUID> {
    @Query("SELECT c FROM Category c WHERE c.name LIKE CONCAT('%', :search, '%')")
    Page<Category> findByCategoryName(@Param("search") String search, Pageable pageable);

    boolean existsByName(String name);
}
