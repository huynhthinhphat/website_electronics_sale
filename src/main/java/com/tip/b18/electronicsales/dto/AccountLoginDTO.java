package com.tip.b18.electronicsales.dto;

import com.tip.b18.electronicsales.constants.MessageConstant;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountLoginDTO {
    @NotBlank(message = MessageConstant.INVALID_USERNAME)
    private String userName;
    @NotBlank(message = MessageConstant.INVALID_PASSWORD)
    private String password;
    private String email;
}
