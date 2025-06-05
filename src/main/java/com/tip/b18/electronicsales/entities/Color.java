package com.tip.b18.electronicsales.entities;

import com.tip.b18.electronicsales.entities.base.BaseIdEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import lombok.Data;

import java.util.List;

@Entity
@Data
public class Color extends BaseIdEntity {
    @Column(name = "color", nullable = false, columnDefinition = "CHAR(25)")
    private String color;

    @OneToMany(mappedBy = "color")
    private List<ProductColor> productColors;
}
