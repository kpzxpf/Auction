package com.volzhin.auction.controller;

import com.volzhin.auction.dto.UserDto;
import com.volzhin.auction.mapper.UserMapper;
import com.volzhin.auction.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final UserMapper userMapper;

    @GetMapping("/{userId}")
    public UserDto getProfile(@PathVariable long userId) {
        return userMapper.toDto(userService.getUserById(userId));
    }
}
