package com.tip.b18.electronicsales.services;

import com.tip.b18.electronicsales.enums.Delivery;
import com.tip.b18.electronicsales.enums.PaymentMethod;
import com.tip.b18.electronicsales.enums.Status;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.UUID;

public interface ExcelService {
    void exportProductsToExcel(HttpServletResponse response, String search, int page, int limit, UUID categoryId, UUID brandId, String orderBy, String startDay, String endDay, String star) throws IOException;
    void exportOrdersToExcel(HttpServletResponse response, String search, int page, int limit, Status status, PaymentMethod transaction, Delivery delivery, String startDay, String endDay) throws IOException;
}
