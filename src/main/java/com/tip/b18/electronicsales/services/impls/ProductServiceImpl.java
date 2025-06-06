package com.tip.b18.electronicsales.services.impls;

import com.tip.b18.electronicsales.constants.MessageConstant;
import com.tip.b18.electronicsales.dto.*;
import com.tip.b18.electronicsales.entities.*;
import com.tip.b18.electronicsales.entities.base.BaseIdEntity;
import com.tip.b18.electronicsales.exceptions.AlreadyExistsException;
import com.tip.b18.electronicsales.exceptions.CloudinaryDeleteException;
import com.tip.b18.electronicsales.exceptions.InsufficientStockException;
import com.tip.b18.electronicsales.exceptions.NotFoundException;
import com.tip.b18.electronicsales.mappers.ProductMapper;
import com.tip.b18.electronicsales.mappers.TupleMapper;
import com.tip.b18.electronicsales.repositories.*;
import com.tip.b18.electronicsales.services.*;
import com.tip.b18.electronicsales.utils.*;
import jakarta.persistence.Tuple;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final ProductCriteria productCriteria;
    private final TupleMapper tupleMapper;
    private final ColorService colorService;
    private final ProductColorService productColorService;
    private final ImageService imageService;
    private final CategoryService categoryService;
    private final BrandService brandService;
    private final CloudinaryService cloudinaryService;
    private final @Lazy OrderDetailService orderDetailService;

    @Override
    public CustomPage<ProductDTO> viewProducts(String search, int page, int limit, UUID categoryId, UUID brandId, String orderBy, String startDay, String endDay, String star) {
        Page<ProductDTO> products = productCriteria.searchProductsByConditions(search, page, limit, categoryId, brandId, orderBy, LocalDateTimeUtil.parseStartDay(startDay), LocalDateTimeUtil.parseEndDay(endDay), star);

        PageInfoDTO pageInfoDTO = new PageInfoDTO();
        pageInfoDTO.setTotalPages(products.getTotalPages());
        pageInfoDTO.setTotalElements(products.getTotalElements());

        CustomPage<ProductDTO> productDTOs = new CustomPage<>();
        productDTOs.setPageInfo(pageInfoDTO);
        productDTOs.setItems(productMapper.toProductDTOList(products));

        return productDTOs;
    }

    @Override
    public ProductDTO viewProductDetails(UUID id) {
        List<Tuple> tuples = productRepository.findProductById(id);
        if (tuples.isEmpty()) {
            throw new NotFoundException(MessageConstant.ERROR_NOT_FOUND_PRODUCT);
        }

        Tuple tuple = tuples.get(0);
        Product product = tuple.get("product", Product.class);

        List<String> images = tupleMapper.mapToList(tuples, "image");
        List<String> colors = tupleMapper.mapToList(tuples, "color");

        Double star = tuple.get("star", Double.class);
        String formatted = "0";
        if(star != null){
            DecimalFormat df = new DecimalFormat("#0.0");
            formatted = df.format(star);
        }

        ProductDTO.ProductDTOBuilder builder = ProductDTO.builder();
        builder.id(product.getId())
                .sku(product.getSku())
                .name(product.getName())
                .stock(product.getStock())
                .price(product.getPrice())
                .warranty(product.getWarranty())
                .discount(product.getDiscount())
                .discountPrice(product.getDiscountPrice())
                .description(product.getDescription())
                .mainImageUrl(product.getMainImageUrl())
                .colors(colors)
                .images(images)
                .star(Double.parseDouble(formatted));
        if(!SecurityUtil.isAdminRole()){
            Long quantitySold = tuple.get("quantitySold", Long.class);
            builder.quantitySold(quantitySold != null ? quantitySold.intValue() : 0)
                    .category(product.getCategory().getName())
                    .brand(product.getBrand().getName());
        }else{
            builder.category(product.getCategory().getId().toString())
                    .brand(product.getBrand().getId().toString());
        }
        return builder.build();
    }

    @Override
    @Transactional
    public void addProduct(ProductDTO productDTO) {
        if(productRepository.existsBySkuAndIsDeleted(productDTO.getSku(), false)){
            throw new AlreadyExistsException(MessageConstant.ERROR_PRODUCT_EXISTS);
        }

        Category category = categoryService.getCategoryById(UUID.fromString(productDTO.getCategory()));
        Brand brand = brandService.getBrandById(UUID.fromString(productDTO.getBrand()));

        BigDecimal discountPrice = BigDecimalUtil.calculateDiscountPrice(productDTO.getPrice(), productDTO.getDiscount());
        if(discountPrice != null){
            productDTO.setDiscountPrice(discountPrice);
        }else{
            productDTO.setDiscountPrice(productDTO.getPrice());
        }

        Product product = productRepository.save(productMapper.toProduct(productDTO, category, brand));
        List<Color> colorList = colorService.addNewColor(productDTO.getColors());

        productColorService.addProductColors(colorList, product);
        imageService.addImages(productDTO.getImages(), product);
    }

    @Override
    @Transactional
    public void deleteProduct(UUID id) {
        Product product = productRepository.findByIdAndIsDeleted(id, false);
        if(product == null){
            throw new NotFoundException(MessageConstant.ERROR_NOT_FOUND_PRODUCT);
        }
        product.setDeleted(true);
        product.setDeletedAt(LocalDateTime.now());
        productRepository.save(product);
    }

    @Override
    @Transactional
    public void updateProduct(UUID id, ProductDTO productDTO) {
        Product product = productRepository.findByIdAndIsDeleted(id, false);
        if(product == null){
            throw new NotFoundException(MessageConstant.ERROR_NOT_FOUND_PRODUCT);
        }

        boolean isChange = false;

        if(!CompareUtil.compare(productDTO.getSku(), product.getSku())){
            if(productRepository.existsBySkuAndIsDeleted(productDTO.getSku(), false)){
                throw new AlreadyExistsException(MessageConstant.ERROR_PRODUCT_EXISTS);
            }
            product.setSku(productDTO.getSku());
            isChange = true;
        }

        if(!CompareUtil.compare(productDTO.getName(), product.getName())){
            product.setName(productDTO.getName());
            isChange = true;
        }

        if(!CompareUtil.compare(productDTO.getStock(), product.getStock())){
            product.setStock(productDTO.getStock());
            isChange = true;
        }

        if(!CompareUtil.compare(productDTO.getPrice(), product.getPrice())){
            product.setPrice(productDTO.getPrice());
            isChange = true;
        }

        if(!CompareUtil.compare(productDTO.getDiscount(), product.getDiscount())){
            BigDecimal discountPrice = BigDecimalUtil.calculateDiscountPrice(product.getPrice(), productDTO.getDiscount());
            if(!CompareUtil.compare(discountPrice, product.getDiscountPrice())){
                product.setDiscountPrice(discountPrice);
            }
            product.setDiscount(productDTO.getDiscount());
            isChange = true;
        }

        if(!CompareUtil.compare(productDTO.getDescription(), product.getDescription())){
            product.setDescription(productDTO.getDescription());
            isChange = true;
        }

        if(!CompareUtil.compare(productDTO.getWarranty(), product.getWarranty())){
            product.setWarranty(productDTO.getWarranty());
            isChange = true;
        }

        if(!CompareUtil.compare(productDTO.getMainImageUrl(), product.getMainImageUrl())){
            try {
                cloudinaryService.deleteImage(ImageUtil.getPublicIdFromUrl(product.getMainImageUrl()));
            } catch (Exception e) {
                throw new CloudinaryDeleteException(MessageConstant.ERROR_CLOUDINARY);
            }
            product.setMainImageUrl(productDTO.getMainImageUrl());
            isChange = true;
        }

        if(productDTO.getCategory() != null){
            Category category = categoryService.getCategoryById(UUID.fromString(productDTO.getCategory()));
            if(!CompareUtil.compare(category, productDTO.getCategory())){
                product.setCategory(category);
                isChange = true;
            }
        }

        if(productDTO.getBrand() != null){
            Brand brand = brandService.getBrandById(UUID.fromString(productDTO.getBrand()));
            if(!CompareUtil.compare(brand, productDTO.getBrand())){
                product.setBrand(brand);
                isChange = true;
            }
        }

        if(productDTO.getImages() != null && !productDTO.getImages().isEmpty()){
            imageService.updateImagesByProductId(product, productDTO.getImages());
        }

        if(productDTO.getColors() != null && !productDTO.getColors().isEmpty()){
            productColorService.updateProductColorsByProductId(productDTO.getColors(), product);
        }

        if(isChange){
            productRepository.save(product);
        }
    }

    @Override
    public void updateStockProducts(List<OrderDetailDTO> orderDetailDTOList, boolean isAdd) {
        List<UUID> uuidList = orderDetailDTOList
                .stream()
                .map(OrderDetailDTO::getId)
                .toList();

        List<Product> products = productRepository.findAllByIdInAndIsDeleted(uuidList, false);

        Map<UUID, Integer> map = orderDetailDTOList
                .stream()
                .collect(Collectors.toMap(OrderDetailDTO::getId, OrderDetailDTO::getQuantity, Integer::sum));

        List<Product> productListToUpdate = products
                .stream()
                .filter(product -> map.containsKey(product.getId()))
                .peek(product -> {
                    int quantity = map.get(product.getId());
                    if(product.getStock() < quantity){
                        throw new InsufficientStockException(String.format(MessageConstant.ERROR_INSUFFICIENT_STOCK, product.getName()));
                    }
                    if(!isAdd){
                        product.setStock(product.getStock() - quantity);
                    }else{
                        product.setStock(product.getStock() + quantity);
                    }
                })
                .toList();
        productRepository.saveAll(productListToUpdate);
    }

    @Override
    public List<Product> findProductsById(List<OrderDetailDTO> orderDetailDTOList) {
        List<UUID> uuidList = orderDetailDTOList
                .stream()
                .map(OrderDetailDTO::getId)
                .toList();

        return productRepository.findAllByIdInAndIsDeleted(uuidList, false);
    }

    @Override
    public Product findProductById(UUID uuid) {
        Product product = productRepository.findByIdAndIsDeleted(uuid, false);
        if(product == null){
            throw new NotFoundException(MessageConstant.ERROR_NOT_FOUND_PRODUCT);
        }
        return product;
    }

    @Override
    public int getQuantityNewProducts(LocalDateTime startDay, LocalDateTime endDay) {
        return productRepository.countQuantityNewProducts(startDay, endDay);
    }

    @Override
    public void saveAll(List<Product> products) {
        productRepository.saveAll(products);
    }

    @Override
    public void updateStockProducts(Map<UUID, Product> productMap, Collection<OrderDetail> orderDetails) {
        for (OrderDetail orderDetail : orderDetails) {
            Product product = orderDetail.getProduct();
            UUID productId = product.getId();

            if (!productMap.containsKey(productId)) {
                product.setStock(product.getStock() + orderDetail.getQuantity());
                productMap.put(productId, product);
            } else {
                Product existingProduct = productMap.get(productId);
                existingProduct.setStock(existingProduct.getStock() + orderDetail.getQuantity());
            }
        }
    }

    @Override
    public CustomPage<ProductDTO> viewProductsAreDeleted() {
        Pageable pageable = Pageable.unpaged();
        Page<Product> products = productRepository.findAllByIsDeleted(true, pageable);
        return new CustomPage<>(
                productMapper.toProductDTOS(products),
                new PageInfoDTO(products.getTotalElements(), products.getTotalPages()));
    }

    @Override
    public void restoreProduct(UUID id) {
        Product product = productRepository.findByIdAndIsDeleted(id, true);
        if(product == null){
            throw new NotFoundException(MessageConstant.ERROR_NOT_FOUND_PRODUCT);
        }

        product.setDeleted(false);
        product.setDeletedAt(null);
        productRepository.save(product);
    }

    @Override
    @Scheduled(cron = "0 0 0 * * *")
    public void deleteProductsPermanently() {
        LocalDate targetDate = LocalDate.now().minusDays(90);
        LocalDateTime startOfTargetDate = targetDate.atStartOfDay();
        LocalDateTime endOfTargetDate = targetDate.atTime(LocalTime.MAX);
        List<Tuple> products = productRepository.findAllByIsDeleted(startOfTargetDate, endOfTargetDate);

        List<Product> productList = new ArrayList<>();
        for(Tuple tuple : products){
            Long quantity = tuple.get(1, Long.class);

            if(quantity == 0){
                Product product = tuple.get(0, Product.class);
                productList.add(product);
            }
        }

        productRepository.deleteAll(productList);
        colorService.deleteUnusedColors();
    }
}