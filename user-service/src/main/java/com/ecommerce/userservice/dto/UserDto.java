package com.ecommerce.userservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserDto {
    @NotBlank(message = "Name is required")
    private String userName;

    @Email(message = "Invalid email format")
    private String email;
}