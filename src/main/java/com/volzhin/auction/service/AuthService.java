package com.volzhin.auction.service;

import com.volzhin.auction.dto.UserDto;
import com.volzhin.auction.entity.User;
import com.volzhin.auction.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordHashingService passwordHashingService;

    public void registerUser(UserDto userDto) {
        userRepository.save(User.builder()
                .username(userDto.getUsername())
                .email(userDto.getEmail())
                .role(User.Role.user)
                .passwordHash(passwordHashingService.hashPassword(userDto.getPassword()))
                .build());
    }
}
