package com.tip.b18.electronicsales.entities;

import com.tip.b18.electronicsales.entities.base.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.Data;

@Entity
@Data
public class Brand extends BaseEntity {
    @Column(name = "name", nullable = false, columnDefinition = "VARCHAR(50)")
    private String name;

    @Column(name = "description", nullable = false, columnDefinition = "TEXT")
    private String description;
}
