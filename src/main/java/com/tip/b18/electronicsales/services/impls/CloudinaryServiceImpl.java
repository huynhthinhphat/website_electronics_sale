package com.tip.b18.electronicsales.services.impls;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.tip.b18.electronicsales.services.CloudinaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CloudinaryServiceImpl implements CloudinaryService {
    private final Cloudinary cloudinary;

    @Override
    public void deleteImage(String publicId) throws Exception {
        cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
    }
}
