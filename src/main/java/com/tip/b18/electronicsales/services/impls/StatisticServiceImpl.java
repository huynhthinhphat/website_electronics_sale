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
import java.util.concurrent.CompletableFuture;

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
        CompletableFuture<Integer> productFuture = CompletableFuture.supplyAsync(() ->
                productService.getQuantityNewProducts(startDay, endDay));
        CompletableFuture<Integer> orderFuture = CompletableFuture.supplyAsync(() ->
                orderService.getQuantityNewOrders(startDay, endDay));
        CompletableFuture<Integer> accountFuture = CompletableFuture.supplyAsync(() ->
                accountService.getQuantityNewCustomers(startDay, endDay));
        try{
            return new DailySummaryDTO(productFuture.get(), orderFuture.get() , accountFuture.get());
        }catch (Exception e){
            return new DailySummaryDTO(0, 0 , 0);
        }
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
