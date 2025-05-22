package com.tip.b18.electronicsales.entities;

import com.tip.b18.electronicsales.entities.base.BaseIdEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.Data;

@Entity
@Data
public class Color extends BaseIdEntity {
    @Column(name = "color", nullable = false, columnDefinition = "CHAR(25)")
    private String color;
}
