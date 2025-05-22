package com.tip.b18.electronicsales.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.List;

@Data
@AllArgsConstructor
public class CustomList <T>{
    List<T> items;
}
