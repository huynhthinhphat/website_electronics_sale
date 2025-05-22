package com.tip.b18.electronicsales.dto;

import lombok.AllArgsConstructor;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.tip.b18.electronicsales.constants.MessageConstant;
import com.tip.b18.electronicsales.validators.annotations.NotBlankOrEmpty;
import jakarta.validation.constraints.Email;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AccountDTO {
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private UUID id;
    @NotBlankOrEmpty(message = MessageConstant.INVALID_EMPTY_NAME)
    private String fullName;
    private String userName;
    private boolean role;
    private Boolean gender;
    @NotBlankOrEmpty(message = MessageConstant.INVALID_CHARACTER_PHONE_NUMBER)
    private String phoneNumber;
    @Email(message = MessageConstant.INVALID_EMAIL)
    @NotBlankOrEmpty(message = MessageConstant.INVALID_EMPTY_EMAIL)
    private String email;
    @NotBlankOrEmpty(message = MessageConstant.INVALID_EMPTY_ADDRESS)
    private String address;
    private String avatarUrl;
    private LocalDate dateOfBirth;
    private Integer totalQuantity;
}
