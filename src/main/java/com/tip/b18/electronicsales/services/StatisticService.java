package com.tip.b18.electronicsales.services;

import com.tip.b18.electronicsales.dto.CustomList;
import com.tip.b18.electronicsales.dto.DailyRevenueDTO;
import com.tip.b18.electronicsales.dto.DailySummaryDTO;
import com.tip.b18.electronicsales.dto.ProductDTO;

public interface StatisticService {
    DailySummaryDTO getDailySummary();
    CustomList<ProductDTO> getTopProducts(int limit, String startDay, String endDay);
    CustomList<DailyRevenueDTO> getDailyRevenue(int limit, String startDay, String endDay);
}
