package com.tip.b18.electronicsales.services;

import com.tip.b18.electronicsales.entities.Color;

import java.util.List;

public interface ColorService {
    List<Color> addNewColor(List<String> colors);
    void deleteColors(List<Color> colors);
    Color findByColorName(String colorName);
}
