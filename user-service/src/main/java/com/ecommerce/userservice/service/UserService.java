package com.ecommerce.userservice.service;

import com.ecommerce.userservice.dto.LoginRequest;
import com.ecommerce.userservice.dto.SignupRequest;
import com.ecommerce.userservice.dto.AuthResponse;

public interface UserService {
    AuthResponse signup(SignupRequest request);
    AuthResponse login(LoginRequest request);
}
