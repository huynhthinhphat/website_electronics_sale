package com.tip.b18.electronicsales.validators;

import com.tip.b18.electronicsales.validators.annotations.NotBlankOrEmpty;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class NotBlankOrEmptyValidator implements ConstraintValidator<NotBlankOrEmpty, String> {
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        return !value.trim().isEmpty();
    }
}
