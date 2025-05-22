package com.tip.b18.electronicsales.repositories;

import com.tip.b18.electronicsales.dto.ProductDTO;
import com.tip.b18.electronicsales.entities.OrderDetail;
import com.tip.b18.electronicsales.entities.Product;
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
import java.util.*;

@Repository
public class ProductCriteria {
    @PersistenceContext
    private EntityManager entityManager;

    public Page<ProductDTO> searchProductsByConditions(String search, int page, int limit, UUID categoryId, UUID brandId, String orderBy){
        boolean isAdmin = SecurityUtil.isAdminRole();

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();

        CriteriaQuery<Tuple> cq = cb.createTupleQuery();
        Root<Product> root = cq.from(Product.class);

        Subquery<Integer> subquery = createQuantitySoldSubquery(cb, cq, root);

        cq.multiselect(root, subquery.alias("quantitySold"));

        List<Predicate> predicates = buildPredicates(cb, root, search, categoryId, brandId, isAdmin);
        cq.where(cb.and(predicates.toArray(new Predicate[0])));

        Order order = applyOrdering(cb, root, subquery, orderBy, isAdmin);
        if(order != null) {
            cq.orderBy(order);
        }

        TypedQuery<Tuple> query = entityManager.createQuery(cq);
        Pageable pageable = PageRequest.of(page, limit);
        query.setFirstResult(pageable.getPageNumber() * pageable.getPageSize());
        query.setMaxResults(pageable.getPageSize());
        List<Tuple> products = query.getResultList();

        List<ProductDTO> productDTOs = mapToProductDTOs(products, root, isAdmin);

        Long totalElements = countTotalElement(cb, search, categoryId, brandId, orderBy, isAdmin);

        return new PageImpl<>(productDTOs, PageRequest.of(page, limit), totalElements);
    }

    private Subquery<Integer> createQuantitySoldSubquery(CriteriaBuilder cb, CriteriaQuery<Tuple> cq, Root<Product> root){
        Subquery<Integer> subquery = cq.subquery(Integer.class);
        Root<OrderDetail> oderDetailRoot = subquery.from(OrderDetail.class);
        subquery.select(cb.sum(cb.coalesce(oderDetailRoot.get("quantity"), 0)));
        subquery.where(cb.equal(oderDetailRoot.get("product"), root));

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

    private List<Predicate> buildPredicates(CriteriaBuilder cb, Root<Product> root, String search, UUID categoryId, UUID brandId, boolean isAdmin) {
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
        if (brandId != null && isAdmin) {
            predicates.add(cb.equal(root.get("brand").get("id"), brandId));
        }
        predicates.add(cb.equal(root.get("isDeleted"), false));
        return predicates;
    }

    private List<ProductDTO> mapToProductDTOs(List<Tuple> products, Root<Product> root, boolean isAdmin){
        return products.stream()
                .map(tuple -> {
                    Product p = tuple.get(root);
                    ProductDTO.ProductDTOBuilder builder = ProductDTO.builder();
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
                        Integer quantitySold = tuple.get("quantitySold", Integer.class);
                        builder.id(p.getId())
                                .name(p.getName())
                                .price(p.getPrice())
                                .discountPrice(p.getDiscountPrice())
                                .discount(p.getDiscount())
                                .quantitySold(quantitySold != null ? quantitySold : 0)
                                .mainImageUrl(p.getMainImageUrl());
                    }
                    return builder.build();
                }).toList();
    }

    private Long countTotalElement(CriteriaBuilder cb, String search, UUID categoryId, UUID brandId, String orderBy, boolean isAdmin){
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<Product> root = cq.from(Product.class);
        cq.select(cb.count(root));
        List<Predicate> predicates = buildPredicates(cb, root, search, categoryId, brandId, isAdmin);
        cq.where(cb.and(predicates.toArray(new Predicate[0])));

        return entityManager.createQuery(cq).getSingleResult();
    }
}
