package com.tip.b18.electronicsales.entities;

import com.tip.b18.electronicsales.entities.base.BaseIdEntity;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class ReviewImage extends BaseIdEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "review")
    private Review review;

    @Column(name = "url")
    private String url;
}
