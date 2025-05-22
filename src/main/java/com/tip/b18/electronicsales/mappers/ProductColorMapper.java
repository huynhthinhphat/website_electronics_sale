package com.tip.b18.electronicsales.mappers;

import com.tip.b18.electronicsales.entities.ProductColor;
import org.mapstruct.Mapper;
import java.util.*;

@Mapper(componentModel = "spring")
public interface ProductColorMapper {
    default Map<UUID, List<String>> toMap(List<ProductColor> productColorList){
        Map<UUID, List<String>> map = new HashMap<>();
        for (ProductColor productColor : productColorList){
            map.computeIfAbsent(
                    productColor.getProduct().getId(),
                    k -> new ArrayList<>()
            ).add(productColor.getColor().getColor());
        }
        return map;
    }
}
