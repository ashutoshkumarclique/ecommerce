package com.ecommerce.userservice.controller;

import com.ecommerce.userservice.dto.AuthResponse;
import com.ecommerce.userservice.dto.LoginRequest;
import com.ecommerce.userservice.dto.SignupRequest;
import com.ecommerce.userservice.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;

    @Autowired
    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/signup")
    public ResponseEntity<AuthResponse> signup(@Valid @RequestBody SignupRequest request) {
        AuthResponse response = userService.signup(request);
        HttpStatus status = response.getMessage().contains("already") ? HttpStatus.CONFLICT : HttpStatus.CREATED;
        return new ResponseEntity<>(response, status);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = userService.login(request);
        HttpStatus status = response.getMessage().contains("Invalid") ? HttpStatus.UNAUTHORIZED : HttpStatus.OK;
        return new ResponseEntity<>(response, status);
    }
}
