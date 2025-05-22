package com.tip.b18.electronicsales.utils;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

public class PageableUtil {
    public static Pageable toPageable(int limit){
        return limit > 0 ? PageRequest.of(0, limit) : Pageable.unpaged();
    }
}
