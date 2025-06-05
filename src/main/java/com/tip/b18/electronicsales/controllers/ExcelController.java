package com.tip.b18.electronicsales.controllers;

import com.tip.b18.electronicsales.enums.Delivery;
import com.tip.b18.electronicsales.enums.PaymentMethod;
import com.tip.b18.electronicsales.enums.Status;
import com.tip.b18.electronicsales.services.ExcelService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/api/excel")
@RequiredArgsConstructor
@SecurityRequirement(name = "JWT")
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
public class ExcelController {
    private final ExcelService excelService;

    @GetMapping("/export_products")
    public void exportExcelOfProducts(HttpServletResponse response,
                            @RequestParam(name = "search", defaultValue = "") String search,
                            @RequestParam(name = "page", defaultValue = "0") int page,
                            @RequestParam(name = "limit", defaultValue = "6") int limit,
                            @RequestParam(name = "categoryId", defaultValue = "") UUID categoryId,
                            @RequestParam(name = "brandId", defaultValue = "") UUID brandId,
                            @Parameter(description = "Chọn kiểu sắp xếp", schema = @Schema(allowableValues = {"newest", "bestseller", "priceAsc", "priceDesc", "priceDiscountAsc", "priceDiscountDesc"}))
                            @RequestParam(name = "orderBy", defaultValue = "") String orderBy,
                            @RequestParam(name = "startDay", required = false) String startDay,
                            @RequestParam(name = "endDay", required = false) String endDay,
                            @RequestParam(name = "star", defaultValue = "") String star) throws IOException {
        response.setHeader("Content-Disposition", "attachment; filename=products.xlsx");
        excelService.exportProductsToExcel(response, search, page, limit, categoryId, brandId, orderBy,startDay, endDay, star);
    }

    @GetMapping("/export_orders")
    public void exportExcelOfOrders(HttpServletResponse response,
                                    @RequestParam(name = "search", defaultValue = "") String search,
                                    @RequestParam(name = "page", defaultValue = "0") int page,
                                    @RequestParam(name = "limit", defaultValue = "6") int limit,
                                    @Parameter(description = "Chọn trạng thái", schema = @Schema(implementation = Status.class))
                                    @RequestParam(name = "status", defaultValue = "") Status status,
                                    @Parameter(description = "Chọn loại giao dịch", schema = @Schema(implementation = PaymentMethod.class))
                                    @RequestParam(name = "transaction", defaultValue = "") PaymentMethod transaction,
                                    @Parameter(description = "Chọn loại vận chuyển", schema = @Schema(implementation = Delivery.class))
                                    @RequestParam(name = "delivery", defaultValue = "") Delivery delivery,
                                    @RequestParam(name = "startDay", required = false) String startDay,
                                    @RequestParam(name = "endDay", required = false) String endDay) throws IOException {
        response.setHeader("Content-Disposition", "attachment; filename=products.xlsx");
        excelService.exportOrdersToExcel(response, search, page, limit, status, transaction, delivery, startDay, endDay);
    }
}
