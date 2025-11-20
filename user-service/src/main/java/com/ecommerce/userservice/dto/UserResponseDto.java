package com.ecommerce.userservice.dto;

import lombok.Data;

@Data
public class UserResponseDto {
    private String username;
    private String email;
    private String role;
}
