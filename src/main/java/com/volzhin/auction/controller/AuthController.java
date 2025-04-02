package com.volzhin.auction.controller;

import com.volzhin.auction.dto.LoginDto;
import com.volzhin.auction.dto.UserDto;
import com.volzhin.auction.mapper.UserMapper;
import com.volzhin.auction.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final UserMapper userMapper;

    @PostMapping("/register")
    public UserDto register(@Valid @RequestBody UserDto userDto) {
        return userMapper.toDto(authService.registerUser(userDto));
    }

    @PostMapping("/login")
    public void login(@RequestBody LoginDto loginDto) {
        authService.login(loginDto);
    }
}