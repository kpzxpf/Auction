package com.volzhin.auction.service;

import com.volzhin.auction.entity.User;
import com.volzhin.auction.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    void save_shouldCallRepositorySave() {
        // Arrange
        User userToSave = User.builder()
                .id(1L) // ID может быть или не быть, save обработает
                .username("testUser")
                .email("test@example.com")
                .build();

        // Act
        userService.save(userToSave);

        // Assert
        verify(userRepository).save(userToSave);
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void getUserById_shouldReturnUser_whenFound() {
        // Arrange
        long userId = 10L;
        User expectedUser = User.builder()
                .id(userId)
                .username("foundById")
                .email("found@example.com")
                .build();
        when(userRepository.findById(userId)).thenReturn(Optional.of(expectedUser));

        // Act
        User actualUser = userService.getUserById(userId);

        // Assert
        assertNotNull(actualUser);
        assertEquals(expectedUser, actualUser);
        verify(userRepository).findById(userId);
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void getUserById_shouldThrowEntityNotFoundException_whenNotFound() {
        // Arrange
        long userId = 11L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act & Assert
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            userService.getUserById(userId);
        });
        assertEquals(String.format("User with id %s not found", userId), exception.getMessage());
        verify(userRepository).findById(userId);
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void getUserByUsername_shouldReturnUser_whenFound() {
        // Arrange
        String username = "existingUsername";
        User expectedUser = User.builder()
                .id(12L)
                .username(username)
                .email("existing@example.com")
                .build();
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(expectedUser));

        // Act
        User actualUser = userService.getUserByUsername(username);

        // Assert
        assertNotNull(actualUser);
        assertEquals(expectedUser, actualUser);
        verify(userRepository).findByUsername(username);
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void getUserByUsername_shouldThrowEntityNotFoundException_whenNotFound() {
        // Arrange
        String username = "nonExistingUsername";
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        // Act & Assert
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            userService.getUserByUsername(username);
        });
        assertEquals(String.format("User with name %s not found", username), exception.getMessage());
        verify(userRepository).findByUsername(username);
        verifyNoMoreInteractions(userRepository);
    }
}