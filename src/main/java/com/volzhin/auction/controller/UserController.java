package com.volzhin.auction.controller;

import com.volzhin.auction.dto.TopUpRequest;
import com.volzhin.auction.dto.UserDto;
import com.volzhin.auction.entity.User;
import com.volzhin.auction.mapper.UserMapper;
import com.volzhin.auction.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

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

    @PostMapping("/{userId}/top-up")
    public ResponseEntity<?> topUpBalance(@PathVariable Long userId, @RequestBody TopUpRequest topUpRequest) {
         userService.topUpBalance(userId, topUpRequest.getAmount());
         return ResponseEntity.ok().build();
    }
}
