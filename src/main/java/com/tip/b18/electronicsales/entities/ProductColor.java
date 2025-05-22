package com.tip.b18.electronicsales.entities;

import com.tip.b18.electronicsales.entities.base.BaseIdEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;

@Entity
@Data
public class ProductColor extends BaseIdEntity {
    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    @ManyToOne
    @JoinColumn(name = "color_id")
    private Color color;
}
