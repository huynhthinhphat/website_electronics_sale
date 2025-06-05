package com.tip.b18.electronicsales.services;

import com.tip.b18.electronicsales.dto.ColorDTO;
import com.tip.b18.electronicsales.dto.CustomList;
import com.tip.b18.electronicsales.entities.Color;

import java.util.List;

public interface ColorService {
    List<Color> addNewColor(List<String> colors);
    void deleteColors(List<Color> colors);
    Color findByColorName(String colorName);
    List<String> findAll();
    void deleteUnusedColors();
}
