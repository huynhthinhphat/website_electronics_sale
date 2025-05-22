package com.tip.b18.electronicsales.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DailySummaryDTO {
    private int totalQuantityNewProducts;
    private int totalQuantityNewOrders;
    private int totalQuantityNewCustomers;
}
