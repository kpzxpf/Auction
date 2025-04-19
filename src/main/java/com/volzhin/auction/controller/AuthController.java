package com.volzhin.auction.controller;

import com.volzhin.auction.dto.LoginDto;
import com.volzhin.auction.dto.UserDto;
import com.volzhin.auction.service.AuthService;
import jakarta.validation.Valid;
import lombok.Getter;
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
    public ResponseEntity<Long> register(@Valid @RequestBody UserDto userDto) {
        return ResponseEntity.ok(authService.registerUser(userDto));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginDto loginDto) {
        String token = authService.login(loginDto);
        return ResponseEntity.ok(new LoginResponse(token));
    }

    private record LoginResponse(String accessToken) {
    }
}