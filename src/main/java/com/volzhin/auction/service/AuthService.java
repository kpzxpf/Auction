package com.volzhin.auction.service;

import com.volzhin.auction.dto.LoginDto;
import com.volzhin.auction.dto.UserDto;
import com.volzhin.auction.entity.User;
import com.volzhin.auction.exception.InvalidPasswordException;
import com.volzhin.auction.exception.UserNotFoundException;
import com.volzhin.auction.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordHashingService passwordHashingService;

    @Transactional
    public long registerUser(UserDto userDto) {
        User user = User.builder()
                .username(userDto.getUsername())
                .email(userDto.getEmail())
                .role(User.Role.user)
                .balance(BigDecimal.ZERO)
                .passwordHash(passwordHashingService.hashPassword(userDto.getPassword()))
                .build();

        return userRepository.save(user).getId();
    }

    @Transactional(readOnly = true)
    public long login(LoginDto loginDto) {
        User user = userRepository.findByUsername(loginDto.getUsername())
                .orElseThrow(() -> new UserNotFoundException("User not found: " + loginDto.getUsername()));

        if (!passwordHashingService.verifyPassword(loginDto.getPassword(), user.getPasswordHash())) {
            throw new InvalidPasswordException("Invalid password");
        }

        return user.getId();
    }
}
