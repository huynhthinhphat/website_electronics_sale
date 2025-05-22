package com.tip.b18.electronicsales.services.impls;

import com.tip.b18.electronicsales.entities.Image;
import com.tip.b18.electronicsales.entities.Product;
import com.tip.b18.electronicsales.repositories.ImageRepository;
import com.tip.b18.electronicsales.services.ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class ImageServiceImpl implements ImageService {
    private final ImageRepository imageRepository;

    @Override
    public void addImages(List<String> images, Product product) {
        List<Image> imageList = new ArrayList<>();
        for(String imageUrl : images){
            Image image = new Image();
            image.setProduct(product);
            image.setUrl(imageUrl.trim());

            imageList.add(image);
        }
        imageRepository.saveAll(imageList);
    }

    @Override
    public void deleteImages(List<UUID> productIdList) {
        imageRepository.deleteAllByProductIdIn(productIdList);
    }

    @Override
    public List<String> getImagesByProductId(UUID productId) {
        return imageRepository.findAllByProductId(productId);
    }

    @Override
    public void deleteImagesByProductId(UUID productId, List<String> urls) {
        imageRepository.deleteAllByProductIdAndUrlIn(productId, urls);
    }

    @Override
    public void updateImagesByProductId(Product product, List<String> images) {
        List<String> imageList = getImagesByProductId(product.getId());

        Set<String> imagesSet = new HashSet<>(images);
        Set<String> imageListSet = new HashSet<>(imageList);

        if(!imageListSet.equals(imagesSet)){
            List<String> imageListToSave = imagesSet.stream()
                    .filter(url -> !imageListSet.contains(url))
                    .toList();
            addImages(imageListToSave, product);

            List<String> imageListToDelete = imageListSet.stream()
                    .filter(url -> !imagesSet.contains(url))
                    .toList();
            deleteImagesByProductId(product.getId(), imageListToDelete);
        }
    }
}
