package com.tip.b18.electronicsales.services.impls;

import com.tip.b18.electronicsales.constants.MessageConstant;
import com.tip.b18.electronicsales.entities.Color;
import com.tip.b18.electronicsales.entities.Product;
import com.tip.b18.electronicsales.entities.ProductColor;
import com.tip.b18.electronicsales.exceptions.NotFoundException;
import com.tip.b18.electronicsales.mappers.ProductColorMapper;
import com.tip.b18.electronicsales.repositories.ProductColorRepository;
import com.tip.b18.electronicsales.services.ColorService;
import com.tip.b18.electronicsales.services.ProductColorService;
import jakarta.persistence.Tuple;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ProductColorServiceImpl implements ProductColorService {
    private final ProductColorRepository productColorRepository;
    private final ColorService colorService;
    private final ProductColorMapper productColorMapper;

    @Override
    public void addProductColors(List<Color> colorList, Product product) {
        List<ProductColor> productColorList = new ArrayList<>();
        for (Color color : colorList) {
            ProductColor productColor = new ProductColor();
            productColor.setProduct(product);
            productColor.setColor(color);

            productColorList.add(productColor);
        }
        productColorRepository.saveAll(productColorList);
    }

    @Override
    public List<Color> getColorsByProductColors(List<UUID> productIdList) {
        List<Tuple> tuples = productColorRepository.findAllByProductIds(productIdList);
        List<Color> colorList = new ArrayList<>();
        List<ProductColor> productColorList = new ArrayList<>();
        for (Tuple tuple : tuples){
            ProductColor productColor = tuple.get("productColor", ProductColor.class);
            productColorList.add(productColor);

            if((Long)tuple.get(1) < 2){
                Color color = productColor.getColor();
                colorList.add(color);
            }
        }
        deleteProductColors(productColorList);
        return colorList;
    }

    @Override
    public void deleteProductColors(List<ProductColor> productColorList) {
        productColorRepository.deleteAll(productColorList);
    }

    @Override
    public void updateProductColorsByProductId(List<String> colors, Product product) {
        List<Tuple> tupleList = productColorRepository.findAllByProductIds(List.of(product.getId()));
        List<Color> colorListToDelete = new ArrayList<>();
        List<ProductColor> productColorList = new ArrayList<>();
        Set<String> existingColorsLowerCase = new HashSet<>();
        Set<String> colorsSet = new HashSet<>(colors);

        for(Tuple tuple : tupleList){
            ProductColor productColor = tuple.get("productColor", ProductColor.class);
            productColorList.add(productColor);

            existingColorsLowerCase.add(productColor.getColor().getColor().toLowerCase());

            if(!colorsSet.contains(productColor.getColor().getColor())){
                if((Long)tuple.get(1) < 2){
                    Color color = productColor.getColor();
                    colorListToDelete.add(color);
                }
            }
        }

        List<ProductColor> productColorsToDelete = productColorList
                .stream()
                .filter(e -> !colorsSet.contains(e.getColor().getColor()))
                .toList();

        List<String> colorsToSave = colors
                .stream()
                .filter(color -> !existingColorsLowerCase.contains(color.toLowerCase()))
                .toList();

        List<Color> colorList = new ArrayList<>();
        if(!colorsToSave.isEmpty()){
            colorList = colorService.addNewColor(colorsToSave);
        }
        if(!colorList.isEmpty()){
            addProductColors(colorList, product);
        }
        if(!productColorsToDelete.isEmpty()){
            deleteProductColors(productColorsToDelete);
        }
        if(!colorListToDelete.isEmpty()){
            colorService.deleteColors(colorListToDelete);
        }
    }

    @Override
    public Map<UUID, List<String>> getColorsByProductIds(List<UUID> productIdList) {
        return productColorMapper.toMap(productColorRepository.findAllByProductIdIn(productIdList));
    }

    @Override
    public void findByProductIdAndColorId(Product product, Color color, String colorName) {
        ProductColor productColor = productColorRepository.findByProductIdAndColorId(product.getId(), color.getId());
        if(productColor == null){
            throw new NotFoundException(String.format(MessageConstant.ERROR_NOT_FOUND_PRODUCT_AND_COLOR, product.getName(), colorName));
        }
    }
}
