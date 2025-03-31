package com.volzhin.auction.controller;

import com.volzhin.auction.dto.AuthResponse;
import com.volzhin.auction.dto.LoginDto;
import com.volzhin.auction.dto.UserDto;
import com.volzhin.auction.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/register")
    public void register(@Valid @RequestBody UserDto userDto) {
        authService.registerUser(userDto);
    }

    @PostMapping("/login")
    public AuthResponse login(@RequestBody LoginDto loginDto) {
        String token = authService.login(loginDto.getUsername(), loginDto.getPassword());

        return new AuthResponse(token);
    }
}
