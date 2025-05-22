package com.tip.b18.electronicsales.entities;

import com.tip.b18.electronicsales.entities.base.BaseIdEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;

@Entity
@Data
public class Image extends BaseIdEntity {
    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    @Column(name = "url", columnDefinition = "TEXT")
    private String url;
}
