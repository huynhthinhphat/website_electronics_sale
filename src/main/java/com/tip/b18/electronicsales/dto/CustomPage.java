package com.tip.b18.electronicsales.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomPage<T> {
    private List<T> items;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private PageInfoDTO pageInfo;
}
