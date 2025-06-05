package com.tip.b18.electronicsales.services.impls;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.tip.b18.electronicsales.constants.MessageConstant;
import com.tip.b18.electronicsales.entities.Image;
import com.tip.b18.electronicsales.entities.Product;
import com.tip.b18.electronicsales.exceptions.CloudinaryDeleteException;
import com.tip.b18.electronicsales.repositories.ImageRepository;
import com.tip.b18.electronicsales.services.CloudinaryService;
import com.tip.b18.electronicsales.services.ImageService;
import com.tip.b18.electronicsales.utils.ImageUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ImageServiceImpl implements ImageService {
    private final ImageRepository imageRepository;
    private final CloudinaryService cloudinaryService;

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

        updateImagesInDB(product, imagesSet, imageListSet);
        deleteImagesOnCloudinary(imageList);
    }

    @Async
    @Override
    public void updateImagesInDB(Product product, Set<String> imagesSet, Set<String> imageListSet) {
        if (!imageListSet.equals(imagesSet)) {
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

    @Async
    @Override
    public void deleteImagesOnCloudinary(List<String> imageList) {
        for (String image : imageList) {
            try {
                cloudinaryService.deleteImage(ImageUtil.getPublicIdFromUrl(image));
            } catch (Exception e) {
                throw new CloudinaryDeleteException(MessageConstant.ERROR_CLOUDINARY);
            }
        }
    }

    @Override
    public String uploadImage(MultipartFile file) {
        try {
            return cloudinaryService.uploadImages(file).get("secure_url").toString();
        } catch (IOException e) {
            return "Upload ảnh lỗi";
        }
    }

    @Override
    public void deleteImages(List<String> images) throws Exception {
        if(!images.isEmpty()){
            for (String image : images){
                cloudinaryService.deleteImage(ImageUtil.getPublicIdFromUrl(image));
            }
        }
    }
}
