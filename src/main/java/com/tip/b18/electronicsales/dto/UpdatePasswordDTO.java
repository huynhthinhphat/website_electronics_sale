package com.tip.b18.electronicsales.dto;

import com.tip.b18.electronicsales.constants.MessageConstant;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdatePasswordDTO {
    @NotBlank(message = MessageConstant.INVALID_NEW_PASSWORD)
    private String newPassword;
    @NotBlank(message = MessageConstant.INVALID_OLD_PASSWORD)
    private String oldPassword;
    private String userName;
}
