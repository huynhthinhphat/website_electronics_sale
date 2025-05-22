package com.tip.b18.electronicsales.utils;

public class ImageUtil {
    public static String getPublicIdFromUrl(String url) {
        if (url == null || url.isEmpty()) {
            return null;
        }
        String[] parts = url.split("/");
        return parts[parts.length - 1].split("\\.")[0];
    }
}
