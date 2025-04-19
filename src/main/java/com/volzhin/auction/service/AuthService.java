package com.volzhin.auction.service;

import com.volzhin.auction.dto.LoginDto;
import com.volzhin.auction.dto.UserDto;
import com.volzhin.auction.entity.User;
import com.volzhin.auction.repository.UserRepository;
import com.volzhin.auction.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordHashingService passwordHashingService;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;

    @Transactional
    public long registerUser(UserDto userDto) {
        if (userRepository.findByUsername(userDto.getUsername()).isPresent()) {
            throw new RuntimeException("Username already exists!");
        }
        User user = User.builder()
                .username(userDto.getUsername())
                .email(userDto.getEmail())
                .role(User.Role.user)
                .passwordHash(passwordHashingService.hashPassword(userDto.getPassword()))
                .balance(BigDecimal.ZERO)
                .build();
        User savedUser = userRepository.save(user);
        log.info("User registered successfully with ID: {}", savedUser.getId());
        return savedUser.getId();
    }

    public String login(LoginDto loginDto) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginDto.getUsername(),
                            loginDto.getPassword()
                    )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            String jwt = tokenProvider.generateAccessToken(authentication);
            log.info("User '{}' logged in successfully.", loginDto.getUsername());
            return jwt;
        } catch (Exception e) {
            log.error("Authentication failed for user '{}': {}", loginDto.getUsername(), e.getMessage());
            throw new RuntimeException("Invalid username or password", e);
        }
    }
}