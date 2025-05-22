package com.tip.b18.electronicsales.entities;

import com.tip.b18.electronicsales.entities.base.BaseIdEntity;
import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Data
public class Revenue extends BaseIdEntity {
    @Column(name = "total_price", precision = 15, scale = 2, nullable = false)
    private BigDecimal totalPrice;

    @Column(name = "total_order", nullable = false)
    private int totalOrder;
    private LocalDate date;
}
