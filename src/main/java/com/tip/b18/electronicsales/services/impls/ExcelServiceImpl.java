package com.tip.b18.electronicsales.services.impls;

import com.tip.b18.electronicsales.dto.ProductDTO;
import com.tip.b18.electronicsales.entities.Order;
import com.tip.b18.electronicsales.entities.Product;
import com.tip.b18.electronicsales.enums.Delivery;
import com.tip.b18.electronicsales.enums.PaymentMethod;
import com.tip.b18.electronicsales.enums.Status;
import com.tip.b18.electronicsales.repositories.ProductCriteria;
import com.tip.b18.electronicsales.services.ExcelService;
import com.tip.b18.electronicsales.services.OrderService;
import com.tip.b18.electronicsales.services.ProductService;
import com.tip.b18.electronicsales.utils.LocalDateTimeUtil;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ExcelServiceImpl implements ExcelService {
    private final ProductCriteria productCriteria;
    private final OrderService orderService;

    @Override
    public void exportProductsToExcel(HttpServletResponse response, String search, int page, int limit, UUID categoryId, UUID brandId, String orderBy, String startDay, String endDay, String star) throws IOException {
        Page<ProductDTO> products = productCriteria.searchProductsByConditions(search, page, limit, categoryId, brandId, orderBy, LocalDateTimeUtil.parseStartDay(startDay), LocalDateTimeUtil.parseEndDay(endDay), star);

        Workbook workbook = new HSSFWorkbook();
        Sheet sheet = workbook.createSheet("Products");
        Row header = sheet.createRow(0);
        header.createCell(0).setCellValue("STT");
        header.createCell(1).setCellValue("Mã sản phẩm");
        header.createCell(2).setCellValue("Tên");
        header.createCell(3).setCellValue("Số lượng");
        header.createCell(4).setCellValue("Giá gốc");
        header.createCell(5).setCellValue("Giá giảm");
        header.createCell(6).setCellValue("% giảm");
        header.createCell(7).setCellValue("Số lượng đã bán");
        header.createCell(8).setCellValue("Ngày tạo");
        header.createCell(9).setCellValue("Ngày cập nhật gần nhất");

        int rowNum = 1;
        for(ProductDTO product : products){
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(rowNum - 1);
            row.createCell(1).setCellValue(product.getSku());
            row.createCell(2).setCellValue(product.getName());
            row.createCell(3).setCellValue(product.getStock());
            row.createCell(4).setCellValue(Double.parseDouble(product.getPrice().toString()));
            row.createCell(5).setCellValue(Double.parseDouble(product.getDiscountPrice().toString()));
            row.createCell(6).setCellValue(Double.parseDouble(product.getDiscount().toString()));
            row.createCell(7).setCellValue(product.getQuantitySold());
            row.createCell(8).setCellValue(product.getCreatedAt().format(DateTimeFormatter.ofPattern("HH:mm:ss dd-MM-yyyy")));
            row.createCell(9).setCellValue(product.getUpdatedAt().format(DateTimeFormatter.ofPattern("HH:mm:ss dd-MM-yyyy")));
        }

        ServletOutputStream outputStream = response.getOutputStream();
        workbook.write(outputStream);
        workbook.close();
        outputStream.close();
    }

    @Override
    public void exportOrdersToExcel(HttpServletResponse response, String search, int page, int limit, Status status, PaymentMethod transaction, Delivery delivery, String startDay, String endDay) throws IOException {
        Page<Order> orders = orderService.getOrdersToExport(search, page, limit, status, transaction, delivery, startDay, endDay);

        Workbook workbook = new HSSFWorkbook();
        Sheet sheet = workbook.createSheet("Danh sách hóa đơn");
        Row header = sheet.createRow(0);
        header.createCell(0).setCellValue("STT");
        header.createCell(1).setCellValue("Mã hóa đơn");
        header.createCell(2).setCellValue("Số điện thoại");
        header.createCell(3).setCellValue("Họ và tên");
        header.createCell(4).setCellValue("Địa chỉ");
        header.createCell(5).setCellValue("Loại giao dịch");
        header.createCell(6).setCellValue("Đơn vị vận chuyển");
        header.createCell(7).setCellValue("Trạng thái đơn hàng");
        header.createCell(8).setCellValue("Tổng tiền");
        header.createCell(9).setCellValue("Ngày tạo");

        int rowNum = 1;
        for(Order order : orders){
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(rowNum - 1);
            row.createCell(1).setCellValue(order.getOrderCode());
            row.createCell(2).setCellValue(order.getPhoneNumber());
            row.createCell(3).setCellValue(order.getFullName());
            row.createCell(4).setCellValue(order.getAddress());
            row.createCell(5).setCellValue(order.getPaymentMethod().getDisplayName());
            row.createCell(6).setCellValue(order.getDelivery().getDisplayName());
            row.createCell(7).setCellValue(order.getStatus().getDisplayName());
            row.createCell(8).setCellValue(Double.parseDouble(order.getTotalPrice().toString()));
            row.createCell(9).setCellValue(order.getCreatedAt().format(DateTimeFormatter.ofPattern("HH:mm:ss dd-MM-yyyy")));
        }

        ServletOutputStream outputStream = response.getOutputStream();
        workbook.write(outputStream);
        workbook.close();
        outputStream.close();
    }
}
