package com.volzhin.auction.service;

import com.volzhin.auction.dto.UserDto;
import com.volzhin.auction.entity.user.User;
import com.volzhin.auction.exception.InvalidPasswordException;
import com.volzhin.auction.exception.UserNotFoundException;
import com.volzhin.auction.repository.UserRepository;
import com.volzhin.auction.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordHashingService passwordHashingService;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    public void registerUser(UserDto userDto) {
        userRepository.save(User.builder()
                .username(userDto.getUsername())
                .email(userDto.getEmail())
                .role(User.Role.user)
                .passwordHash(passwordHashingService.hashPassword(userDto.getPassword()))
                .build());
    }

    @Transactional(readOnly = true)
    public String login(String username, String password) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    log.error("User not found with username {}", username);
                    return new UserNotFoundException("User not found: " + username);
                });


        if (!passwordHashingService.verifyPassword(password, user.getPasswordHash())) {
            log.error("Invalid password");
            throw new InvalidPasswordException("Invalid password");
        }

        return jwtTokenProvider.generateToken(username);
    }
}
