package com.tip.b18.electronicsales.services.impls;

import com.tip.b18.electronicsales.constants.MessageConstant;
import com.tip.b18.electronicsales.entities.Color;
import com.tip.b18.electronicsales.exceptions.NotFoundException;
import com.tip.b18.electronicsales.repositories.ColorRepository;
import com.tip.b18.electronicsales.services.ColorService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ColorServiceImpl implements ColorService {
    private final ColorRepository colorRepository;

    @Override
    public List<Color> addNewColor(List<String> colors) {
        List<Color> colorList = new ArrayList<>();
        List<Color> colorListReturn = new ArrayList<>();
        for(String colorName : colors){
            Color colorExist = colorRepository.existsByColor(colorName.trim());
            if(colorExist == null){
                Color color = new Color();
                color.setColor(colorName);
                colorList.add(color);
            }else{
                colorListReturn.add(colorExist);
            }
        }
        colorListReturn.addAll(colorRepository.saveAll(colorList));
        return colorListReturn;
    }

    @Override
    public void deleteColors(List<Color> colors) {
        colorRepository.deleteAll(colors);
    }

    @Override
    public Color findByColorName(String colorName) {
        Color color = colorRepository.existsByColor(colorName);
        if(color == null){
            throw new NotFoundException(String.format(MessageConstant.ERROR_NOT_FOUND_COLOR, colorName));
        }
        return color;
    }
}
