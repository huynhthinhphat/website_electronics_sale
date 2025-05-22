package com.tip.b18.electronicsales.services.impls;

import com.tip.b18.electronicsales.constants.MessageConstant;
import com.tip.b18.electronicsales.dto.OrderDetailDTO;
import com.tip.b18.electronicsales.entities.Order;
import com.tip.b18.electronicsales.entities.OrderDetail;
import com.tip.b18.electronicsales.entities.Product;
import com.tip.b18.electronicsales.exceptions.NotFoundException;
import com.tip.b18.electronicsales.mappers.OrderDetailMapper;
import com.tip.b18.electronicsales.repositories.OrderDetailRepository;
import com.tip.b18.electronicsales.services.OrderDetailService;
import com.tip.b18.electronicsales.services.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderDetailServiceImpl implements OrderDetailService {
    private final OrderDetailRepository orderDetailRepository;
    private final OrderDetailMapper orderDetailMapper;
    private final ProductService productService;

    @Override
    public List<OrderDetailDTO> findAllByOrderId(List<UUID> uuidList) {
        return orderDetailMapper.toOrderDetailDTOs(orderDetailRepository.findAllByOrderIdIn(uuidList));
    }

    @Override
    public List<OrderDetailDTO> findAllByOrderId(UUID orderId) {
        return orderDetailMapper.toOrderDetailDTOs(orderDetailRepository.findAllByOrderId(orderId));
    }

    @Override
    public List<OrderDetailDTO> createOrderDetails(Order order, List<OrderDetailDTO> orderDetailDTOList) {
        List<Product> productList = productService.findProductsById(orderDetailDTOList);
        List<OrderDetail> orderDetails = new ArrayList<>();

        Map<UUID, Product> map = productList
                .stream()
                .collect(Collectors.toMap(Product::getId, Product -> Product));

        for (OrderDetailDTO orderDetailDTO : orderDetailDTOList){
            Product product = map.get(orderDetailDTO.getId());
            if(product == null){
                throw new NotFoundException(MessageConstant.ERROR_NOT_FOUND_PRODUCT);
            }
            OrderDetail orderDetail = orderDetailMapper.toOrderDetail(order, orderDetailDTO, product);
            orderDetails.add(orderDetail);
        }

        return orderDetailMapper.createOrderDetailResponse(orderDetailRepository.saveAll(orderDetails));
    }
}
