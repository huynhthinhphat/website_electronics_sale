package com.tip.b18.electronicsales.dto;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.util.Date;

@Data
@Builder
public class DailyRevenueDTO {
    private Long totalOrder;
    private BigDecimal totalPrice;
    private Date date;
}
