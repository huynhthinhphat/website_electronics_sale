package com.tip.b18.electronicsales.repositories;

import com.tip.b18.electronicsales.entities.Color;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.UUID;

@Repository
public interface ColorRepository extends JpaRepository<Color, UUID> {
    @Query("SELECT c FROM Color c WHERE LOWER(c.color) = LOWER(:color)")
    Color existsByColor(@Param("color") String color);
}
