package com.tip.b18.electronicsales.services.impls;

import com.tip.b18.electronicsales.dto.CustomList;
import com.tip.b18.electronicsales.dto.CustomList;
import com.tip.b18.electronicsales.dto.DailyRevenueDTO;
import com.tip.b18.electronicsales.dto.DailySummaryDTO;
import com.tip.b18.electronicsales.dto.ProductDTO;
import com.tip.b18.electronicsales.services.AccountService;
import com.tip.b18.electronicsales.services.OrderService;
import com.tip.b18.electronicsales.services.ProductService;
import com.tip.b18.electronicsales.services.StatisticService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Service
@RequiredArgsConstructor
public class StatisticServiceImpl implements StatisticService {
    private final ProductService productService;
    private final AccountService accountService;
    private final OrderService orderService;

    @Override
    public DailySummaryDTO getDailySummary() {
        LocalDateTime startDay = LocalDate.now().atStartOfDay();
        LocalDateTime endDay = LocalDate.now().atTime(LocalTime.MAX);

        int totalQuantityNewProducts = productService.getQuantityNewProducts(startDay, endDay);
        int totalQuantityNewOrders = orderService.getQuantityNewOrders(startDay, endDay);
        int totalQuantityNewCustomers = accountService.getQuantityNewCustomers(startDay, endDay);

        return new DailySummaryDTO(totalQuantityNewProducts, totalQuantityNewOrders , totalQuantityNewCustomers);
    }

    @Override
    public CustomList<ProductDTO> getTopProducts(int limit, String startDay, String endDay) {
        return orderService.getTopProducts(limit, startDay, endDay);
    }

    @Override
    public CustomList<DailyRevenueDTO> getDailyRevenue(int limit, String startDay, String endDay) {
        return orderService.getDailyRevenue(limit, startDay, endDay);
    }
}
