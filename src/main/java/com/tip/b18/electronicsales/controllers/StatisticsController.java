package com.tip.b18.electronicsales.controllers;

import com.tip.b18.electronicsales.dto.CustomList;
import com.tip.b18.electronicsales.dto.DailyRevenueDTO;
import com.tip.b18.electronicsales.dto.DailySummaryDTO;
import com.tip.b18.electronicsales.dto.ProductDTO;
import com.tip.b18.electronicsales.dto.ResponseDTO;
import com.tip.b18.electronicsales.services.StatisticService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/statistics")
@RequiredArgsConstructor
@SecurityRequirement(name = "JWT")
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
public class StatisticsController {
    private final StatisticService statisticService;

    @GetMapping
    public ResponseDTO<DailySummaryDTO> getDailySummary(){
        ResponseDTO<DailySummaryDTO> responseDTO = new ResponseDTO<>();

        responseDTO.setStatus("success");
        responseDTO.setData((statisticService.getDailySummary()));

        return responseDTO;
    }

    @GetMapping("/top-products")
    public ResponseDTO<CustomList<ProductDTO>> getTopProducts(@RequestParam(name = "limit", defaultValue = "-1", required = false) int limit,
                                                              @RequestParam(name = "startDay", required = false) String startDay,
                                                              @RequestParam(name = "endDay", required = false) String endDay){
        ResponseDTO<CustomList<ProductDTO>> responseDTO = new ResponseDTO<>();

        responseDTO.setStatus("success");
        responseDTO.setData(statisticService.getTopProducts(limit, startDay, endDay));

        return responseDTO;
    }

    @GetMapping("/revenue")
    public ResponseDTO<CustomList<DailyRevenueDTO>> getDailyRevenue(@RequestParam(name = "limit", defaultValue = "-1", required = false) int limit,
                                                        @RequestParam(name = "startDay", required = false) String startDay,
                                                        @RequestParam(name = "endDay", required = false) String endDay){
        ResponseDTO<CustomList<DailyRevenueDTO>> responseDTO = new ResponseDTO<>();

        responseDTO.setStatus("success");
        responseDTO.setData(statisticService.getDailyRevenue(limit, startDay, endDay));

        return responseDTO;
    }


}
