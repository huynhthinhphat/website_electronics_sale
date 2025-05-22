package com.tip.b18.electronicsales.mappers;

import com.tip.b18.electronicsales.dto.DailyRevenueDTO;
import com.tip.b18.electronicsales.dto.ProductDTO;
import jakarta.persistence.Tuple;
import org.mapstruct.Mapper;
import java.math.BigDecimal;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Mapper(componentModel = "spring")
public interface TupleMapper {
    default List<String> mapToList(List<Tuple> result, String alias){
        return result.stream()
                .map(tuple -> tuple.get(alias, String.class))
                .distinct()
                .filter(Objects::nonNull)
                .toList();
    };

    default List<ProductDTO> toProductDTOList(List<Tuple> tuples){
        return tuples
                .stream()
                .map(tuple -> ProductDTO
                        .builder()
                        .sku(tuple.get("productSku", String.class))
                        .name(tuple.get("productName", String.class))
                        .quantitySold(tuple.get("quantitySold", Long.class).intValue())
                        .build())
                .toList();
    }

    default List<DailyRevenueDTO> toDailyRevenueDTO(List<Tuple> tuples){
        return tuples
                .stream()
                .map(tuple -> DailyRevenueDTO
                        .builder()
                        .totalOrder(tuple.get("totalOrder", Long.class))
                        .totalPrice(tuple.get("totalPrice", BigDecimal.class))
                        .date(tuple.get("date", Date.class))
                        .build())
                .sorted(Comparator.comparing(DailyRevenueDTO::getDate))
                .toList();
    }
}
