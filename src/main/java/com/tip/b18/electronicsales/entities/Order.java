package com.tip.b18.electronicsales.entities;

import com.tip.b18.electronicsales.entities.base.BaseIdEntity;
import com.tip.b18.electronicsales.enums.Delivery;
import com.tip.b18.electronicsales.enums.PaymentMethod;
import com.tip.b18.electronicsales.enums.Status;
import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "`order`")
@Data
public class Order extends BaseIdEntity {
    @Column(name = "order_code", unique = true)
    private String orderCode;

    @ManyToOne
    @JoinColumn(name = "account_id")
    private Account account;

    @Column(name = "total_price", precision = 15, scale = 2, nullable = false)
    private BigDecimal totalPrice;

    @Column(name = "fee_delivery", precision = 15, scale = 2, nullable = false)
    private BigDecimal feeDelivery;

    @Column(name = "phone_number", columnDefinition = "CHAR(10)")
    private String phoneNumber;

    @Column(name = "full_name", columnDefinition = "VARCHAR(50)")
    private String fullName;

    @Column(name = "address", nullable = false)
    private String address;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, columnDefinition = "VARCHAR(50)")
    private Status status;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method", nullable = false, columnDefinition = "VARCHAR(50)")
    private PaymentMethod paymentMethod;

    @Enumerated(EnumType.STRING)
    @Column(name = "delivery", nullable = false, columnDefinition = "VARCHAR(50)")
    private Delivery delivery;

    @Column(name = "note")
    private String note;

    @Column(name = "fromEstimateDate")
    private LocalDateTime fromEstimateDate;

    @Column(name = "toEstimateDate")
    private LocalDateTime toEstimateDate;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false, nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "payment_deadline")
    private LocalDateTime paymentDeadline;

    @ToString.Exclude
    @OneToMany(mappedBy = "order")
    private List<OrderDetail> orderDetails;
}
