package com.volzhin.auction.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PasswordHashingServiceTest {

    @Mock
    private Argon2PasswordEncoder passwordEncoder;

    @InjectMocks
    private PasswordHashingService passwordHashingService;

    @Test
    void hashPassword_shouldCallEncoderEncodeAndReturnHash() {
        // Arrange
        String rawPassword = "mySecretPassword";
        String expectedHash = "$argon2id$v=19$m=16384,t=2,p=1$S01EU0FERFNBREFTQQ$PSu+jXRBHEwwn3PY9F3rfA"; // Example hash
        when(passwordEncoder.encode(rawPassword)).thenReturn(expectedHash);

        // Act
        String actualHash = passwordHashingService.hashPassword(rawPassword);

        // Assert
        assertEquals(expectedHash, actualHash);
        verify(passwordEncoder).encode(rawPassword);
        verifyNoMoreInteractions(passwordEncoder);
    }

    @Test
    void verifyPassword_shouldCallEncoderMatchesAndReturnTrue_whenPasswordsMatch() {
        // Arrange
        String rawPassword = "mySecretPassword";
        String hashedPassword = "$argon2id$v=19$m=16384,t=2,p=1$S01EU0FERFNBREFTQQ$PSu+jXRBHEwwn3PY9F3rfA";
        when(passwordEncoder.matches(rawPassword, hashedPassword)).thenReturn(true);

        // Act
        boolean matches = passwordHashingService.verifyPassword(rawPassword, hashedPassword);

        // Assert
        assertTrue(matches);
        verify(passwordEncoder).matches(rawPassword, hashedPassword);
        verifyNoMoreInteractions(passwordEncoder);
    }

    @Test
    void verifyPassword_shouldCallEncoderMatchesAndReturnFalse_whenPasswordsDoNotMatch() {
        // Arrange
        String rawPassword = "wrongPassword";
        String hashedPassword = "$argon2id$v=19$m=16384,t=2,p=1$S01EU0FERFNBREFTQQ$PSu+jXRBHEwwn3PY9F3rfA";
        when(passwordEncoder.matches(rawPassword, hashedPassword)).thenReturn(false);

        // Act
        boolean matches = passwordHashingService.verifyPassword(rawPassword, hashedPassword);

        // Assert
        assertFalse(matches);
        verify(passwordEncoder).matches(rawPassword, hashedPassword);
        verifyNoMoreInteractions(passwordEncoder);
    }
}