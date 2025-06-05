package com.tip.b18.electronicsales.repositories;

import com.tip.b18.electronicsales.dto.ProductDTO;
import com.tip.b18.electronicsales.entities.OrderDetail;
import com.tip.b18.electronicsales.entities.Product;
import com.tip.b18.electronicsales.entities.Review;
import com.tip.b18.electronicsales.utils.LocalDateTimeUtil;
import com.tip.b18.electronicsales.utils.SecurityUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Tuple;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.util.*;

@Repository
public class ProductCriteria {
    @PersistenceContext
    private EntityManager entityManager;

    public Page<ProductDTO> searchProductsByConditions(String search, int page, int limit, UUID categoryId, UUID brandId, String orderBy, LocalDateTime startDate, LocalDateTime endDate, String star){
        boolean isAdmin = SecurityUtil.isAdminRole();

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();

        CriteriaQuery<Tuple> cq = cb.createTupleQuery();
        Root<Product> productRoot = cq.from(Product.class);

        Subquery<Integer> subqueryQuantitySold = createQuantitySoldSubquery(cb, cq, productRoot);
        Subquery<Double> subqueryRating = createRatingSubquery(cb, cq, productRoot);

        cq.multiselect(productRoot, subqueryQuantitySold.alias("quantitySold"),subqueryRating.alias("star"));

        List<Predicate> predicates = buildPredicates(cb, productRoot, search, categoryId, brandId, startDate, endDate);
        cq.where(cb.and(predicates.toArray(new Predicate[0])));

        Order order = applyOrdering(cb, productRoot, subqueryQuantitySold, orderBy, isAdmin);
        if(order != null) {
            cq.orderBy(order);
        }

        TypedQuery<Tuple> query = entityManager.createQuery(cq);
        Pageable pageable = PageRequest.of(page, limit);
        query.setFirstResult(pageable.getPageNumber() * pageable.getPageSize());
        query.setMaxResults(pageable.getPageSize());
        List<Tuple> products = query.getResultList();

        List<ProductDTO> productDTOs = mapToProductDTOs(products, productRoot, isAdmin, star);

        Long totalElements = countTotalElement(cb, search, categoryId, brandId, startDate, endDate);

        return new PageImpl<>(productDTOs, PageRequest.of(page, limit), totalElements);
    }

    private Subquery<Integer> createQuantitySoldSubquery(CriteriaBuilder cb, CriteriaQuery<Tuple> cq, Root<Product> root){
        Subquery<Integer> subquery = cq.subquery(Integer.class);
        Root<OrderDetail> oderDetailRoot = subquery.from(OrderDetail.class);
        subquery.select(cb.sum(cb.coalesce(oderDetailRoot.get("quantity"), 0)));
        subquery.where(cb.equal(oderDetailRoot.get("product"), root));

        return subquery;
    }

    private Subquery<Double> createRatingSubquery(CriteriaBuilder cb, CriteriaQuery<?> cq, Root<Product> productRoot) {
        Subquery<Double> subquery = cq.subquery(Double.class);
        Root<Review> reviewRoot = subquery.from(Review.class);

        Join<Review, OrderDetail> orderDetailJoin = reviewRoot.join("orderDetail");

        Expression<Double> avgStar = cb.avg(cb.coalesce(reviewRoot.get("star"), 0.0));
        subquery.select(cb.coalesce(avgStar, 0.0))
                .where(cb.equal(orderDetailJoin.get("product").get("id"), productRoot.get("id")));

        return subquery;
    }

    private Order applyOrdering(CriteriaBuilder cb, Root<Product> root, Subquery<Integer> subquery, String orderBy, boolean isAdmin){
        Order order = null;
        if (orderBy != null && !orderBy.isBlank()) {
            if (isAdmin) {
                if(orderBy.equals("priceDiscountAsc")){
                    order = cb.asc(root.get("discountPrice"));
                }else if(orderBy.equals("priceDiscountDesc")){
                    order = cb.desc(root.get("discountPrice"));
                }
            }else{
                switch (orderBy) {
                    case "newest" -> order = cb.desc(root.get("createdAt"));
                    case "bestseller" -> {
                        order = cb.desc(subquery);
                    }
                }
            }
            if(orderBy.equals("priceAsc")){
                order = cb.asc(root.get("discountPrice"));
            }else if(orderBy.equals("priceDesc")){
                order = cb.desc(root.get("discountPrice"));
            }
        }
        return order;
    }

    private List<Predicate> buildPredicates(CriteriaBuilder cb, Root<Product> root, String search, UUID categoryId, UUID brandId, LocalDateTime startDay, LocalDateTime endDay) {
        List<Predicate> predicates = new ArrayList<>();

        if (search != null && !search.isBlank()) {
            String searchValue = "%" + search.toLowerCase().trim() + "%";
            predicates.add(cb.or(
                    cb.like(cb.lower(root.get("name")), searchValue),
                    cb.like(cb.lower(root.get("sku")), searchValue)));
        }
        if (categoryId != null) {
            predicates.add(cb.equal(root.get("category").get("id"), categoryId));
        }
        if (brandId != null) {
            predicates.add(cb.equal(root.get("brand").get("id"), brandId));
        }
        if (startDay != null && endDay != null) {
            predicates.add(cb.between(root.get("createdAt"), startDay, endDay));
        } else if (startDay != null) {
            predicates.add(cb.between(root.get("createdAt"), startDay, LocalDateTime.now()));
        } else if (endDay != null) {
            predicates.add(cb.lessThanOrEqualTo(root.get("createdAt"), endDay));
        }
        predicates.add(cb.equal(root.get("isDeleted"), false));
        return predicates;
    }

    private List<ProductDTO> mapToProductDTOs(List<Tuple> products, Root<Product> root, boolean isAdmin, String star){
        return products.stream()
                .map(tuple -> {
                    Product p = tuple.get(root);
                    ProductDTO.ProductDTOBuilder builder = ProductDTO.builder();

                    Double numberStar = tuple.get("star", Double.class);

                    if (star != null && !star.isBlank()) {
                        String[] range = star.split("-");
                        int minStar = Integer.parseInt(range[0]);
                        int maxStar = Integer.parseInt(range[1]);

                        if (numberStar == null || numberStar < minStar || numberStar > maxStar) {
                            return null;
                        }
                    }

                    Integer quantitySold = tuple.get("quantitySold", Integer.class);
                    String formatted = numberStar != null
                            ? new DecimalFormat("#0.0").format(numberStar)
                            : "0";

                    builder
                            .discount(p.getDiscount())
                            .createdAt(p.getCreatedAt())
                            .updatedAt(p.getUpdatedAt())
                            .quantitySold(quantitySold != null ? quantitySold : 0)
                            .star(Double.parseDouble(formatted));
                    if (isAdmin) {
                        builder.id(p.getId())
                                .sku(p.getSku())
                                .name(p.getName())
                                .stock(p.getStock())
                                .category(p.getCategory().getName())
                                .brand(p.getBrand().getName())
                                .price(p.getPrice())
                                .discountPrice(p.getDiscountPrice());
                    } else {
                        builder.id(p.getId())
                                .name(p.getName())
                                .price(p.getPrice())
                                .discountPrice(p.getDiscountPrice())
                                .discount(p.getDiscount())
                                .mainImageUrl(p.getMainImageUrl());
                    }
                    return builder.build();
                })
                .filter(Objects::nonNull)
                .toList();
    }

    private Long countTotalElement(CriteriaBuilder cb, String search, UUID categoryId, UUID brandId, LocalDateTime startDate, LocalDateTime endDate){
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<Product> root = cq.from(Product.class);
        cq.select(cb.count(root));
        List<Predicate> predicates = buildPredicates(cb, root, search, categoryId, brandId, startDate, endDate);
        cq.where(cb.and(predicates.toArray(new Predicate[0])));

        return entityManager.createQuery(cq).getSingleResult();
    }
}
