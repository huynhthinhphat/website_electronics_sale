package com.tip.b18.electronicsales.services;

import com.tip.b18.electronicsales.entities.Color;
import com.tip.b18.electronicsales.entities.Product;
import com.tip.b18.electronicsales.entities.ProductColor;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface ProductColorService {
    void addProductColors(List<Color> colorList, Product product);
    List<Color> getColorsByProductColors(List<UUID> productIdList);
    void deleteProductColors(List<ProductColor> productColorList);
    void updateProductColorsByProductId(List<String> color, Product product);
    Map<UUID, List<String>> getColorsByProductIds(List<UUID> productIdList);
    void findByProductIdAndColorId(Product product, Color color, String colorName);
}
