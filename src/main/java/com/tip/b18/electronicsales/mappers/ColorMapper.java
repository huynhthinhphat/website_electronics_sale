package com.tip.b18.electronicsales.mappers;

import com.tip.b18.electronicsales.entities.Color;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ColorMapper {
    default List<String> toColorDTOS(List<Color> colors){
        return colors.stream().map(Color::getColor).toList();
    }
}
