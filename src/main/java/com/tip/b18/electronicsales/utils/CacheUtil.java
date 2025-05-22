package com.tip.b18.electronicsales.utils;

import com.tip.b18.electronicsales.dto.OrderDTO;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class CacheUtil {
    private static final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    public static void saveOrderToCache(CacheManager cacheManager, OrderDTO orderDTO) {
        Cache cache = cacheManager.getCache("orders");
        if (cache != null) {
            cache.put(orderDTO.getOrderCode(), orderDTO);

            scheduler.schedule(() -> {
                if (cache.get(orderDTO.getOrderCode()) != null) {
                    cache.evict(orderDTO.getOrderCode());
                }
            }, 10, TimeUnit.MINUTES);
        }
    }

    public static boolean isKeyInCache(CacheManager cacheManager, String orderCode) {
        Cache cache = cacheManager.getCache("orders");
        if (cache != null) {
            return cache.get(orderCode) != null;
        }
        return false;
    }

    public static OrderDTO getOrderFromCache(CacheManager cacheManager, String orderCode) {
        Cache cache = cacheManager.getCache("orders");
        if (cache != null) {
            return cache.get(orderCode, OrderDTO.class);
        }
        return null;
    }

    public static void printAllCacheEntries(CacheManager cacheManager) {
        Cache cache = cacheManager.getCache("orders");
        if (cache != null) {
            Object nativeCache = cache.getNativeCache();
            if (nativeCache instanceof Map<?, ?> map) {
                for (Object key : map.keySet()) {
                    Object value = map.get(key);
                    System.out.println("Key: " + key + ", Value: " + value);
                }
            } else {
                System.out.println("Native cache is not a Map.");
            }
        } else {
            System.out.println("Cache not found.");
        }
    }
}
