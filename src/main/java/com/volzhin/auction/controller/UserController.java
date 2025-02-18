package com.volzhin.auction.controller;

import com.volzhin.auction.dto.UserDto;
import com.volzhin.auction.mapper.UserMapper;
import com.volzhin.auction.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final UserMapper userMapper;

    @GetMapping("/profile")
    public UserDto getProfile(@RequestHeader("Authorization") String bearerToken) {
        return userMapper.toDto(userService.getProfile(bearerToken));
    }
}
