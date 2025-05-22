package com.tip.b18.electronicsales.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PageInfoDTO {
    private long totalElements;
    private int totalPages;
}
