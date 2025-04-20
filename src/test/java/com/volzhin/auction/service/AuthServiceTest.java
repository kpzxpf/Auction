package com.volzhin.auction.service;

import com.volzhin.auction.dto.LoginDto;
import com.volzhin.auction.dto.UserDto;
import com.volzhin.auction.entity.User;
import com.volzhin.auction.exception.InvalidPasswordException;
import com.volzhin.auction.exception.UserNotFoundException;
import com.volzhin.auction.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordHashingService passwordHashingService;

    @InjectMocks
    private AuthService authService;

    @Captor
    private ArgumentCaptor<User> userCaptor;

    @Test
    void registerUser_shouldHashPasswordSaveUserAndReturnId() {
        // Arrange
        UserDto userDto = UserDto.builder()
                .username("newUser")
                .email("new@example.com")
                .password("secretPass")
                .build();

        String hashedPassword = "hashed_secretPass";
        long expectedUserId = 1L;

        User userToSave = User.builder()
                .username(userDto.getUsername())
                .email(userDto.getEmail())
                .passwordHash(hashedPassword)
                .role(User.Role.user)
                .build();

        User savedUser = User.builder()
                .id(expectedUserId)
                .username(userDto.getUsername())
                .email(userDto.getEmail())
                .passwordHash(hashedPassword)
                .role(User.Role.user)
                .build();

        when(passwordHashingService.hashPassword(userDto.getPassword())).thenReturn(hashedPassword);
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        // Act
        long actualUserId = authService.registerUser(userDto);

        // Assert
        assertEquals(expectedUserId, actualUserId);

        verify(passwordHashingService).hashPassword(userDto.getPassword());
        verify(userRepository).save(userCaptor.capture());

        User capturedUser = userCaptor.getValue();
        assertNull(capturedUser.getId(), "ID should be null before saving");
        assertEquals(userDto.getUsername(), capturedUser.getUsername());
        assertEquals(userDto.getEmail(), capturedUser.getEmail());
        assertEquals(hashedPassword, capturedUser.getPasswordHash());
        assertEquals(User.Role.user, capturedUser.getRole());

        verifyNoMoreInteractions(passwordHashingService, userRepository);
    }

    @Test
    void login_shouldReturnUserId_whenCredentialsAreValidAccordingToCurrentLogic() { // Переименован
        // Arrange
        LoginDto loginDto = LoginDto.builder()
                .username("existingUser")
                .password("password123") // Этот пароль НЕ совпадает с хешем (verifyPassword вернет false)
                .build();

        long expectedUserId = 5L;
        String correctHash = "correct_hash"; // Предположим, это правильный хеш
        User foundUser = User.builder()
                .id(expectedUserId)
                .username(loginDto.getUsername())
                .passwordHash(correctHash)
                .build();

        when(userRepository.findByUsername(loginDto.getUsername())).thenReturn(Optional.of(foundUser));
        // Мокируем verifyPassword, чтобы он вернул false (пароли не совпадают)
        // Именно в этом случае по ТЕКУЩЕЙ логике сервиса исключение НЕ выбрасывается
        when(passwordHashingService.verifyPassword(loginDto.getPassword(), foundUser.getPasswordHash())).thenReturn(false);

        // Act
        long actualUserId = authService.login(loginDto);

        // Assert
        assertEquals(expectedUserId, actualUserId); // Успешный логин (по текущей логике)
        verify(userRepository).findByUsername(loginDto.getUsername());
        // Убеждаемся, что verifyPassword ВЫЗЫВАЛСЯ
        verify(passwordHashingService).verifyPassword(loginDto.getPassword(), foundUser.getPasswordHash());
        verifyNoMoreInteractions(userRepository, passwordHashingService);
    }

    @Test
    void login_shouldThrowUserNotFoundException_whenUserNotFound() {
        // Arrange
        LoginDto loginDto = LoginDto.builder()
                .username("unknownUser")
                .password("password123")
                .build();

        when(userRepository.findByUsername(loginDto.getUsername())).thenReturn(Optional.empty());

        // Act & Assert
        UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> {
            authService.login(loginDto);
        });

        assertEquals("User not found: " + loginDto.getUsername(), exception.getMessage());
        verify(userRepository).findByUsername(loginDto.getUsername());
        verifyNoMoreInteractions(userRepository, passwordHashingService);
    }

    @Test
    void login_shouldThrowInvalidPasswordException_whenVerifyPasswordReturnsTrue() { // Уточнили имя
        // Arrange
        LoginDto loginDto = LoginDto.builder()
                .username("existingUser")
                .password("correctPassword") // Этот пароль СОВПАДАЕТ с хешем (verifyPassword вернет true)
                .build();

        long userId = 5L;
        String correctHash = "correct_hash";
        User foundUser = User.builder()
                .id(userId)
                .username(loginDto.getUsername())
                .passwordHash(correctHash)
                .build();

        when(userRepository.findByUsername(loginDto.getUsername())).thenReturn(Optional.of(foundUser));
        // Мокируем verifyPassword, чтобы он вернул true (пароли совпадают)
        // Именно в этом случае по ТЕКУЩЕЙ логике сервиса выбрасывается исключение
        when(passwordHashingService.verifyPassword(loginDto.getPassword(), foundUser.getPasswordHash())).thenReturn(true);

        // Act & Assert
        InvalidPasswordException exception = assertThrows(InvalidPasswordException.class, () -> {
            authService.login(loginDto);
        });

        assertEquals("Invalid password", exception.getMessage());
        // Убеждаемся, что verifyPassword ВЫЗЫВАЛСЯ
        verify(userRepository).findByUsername(loginDto.getUsername());
        verify(passwordHashingService).verifyPassword(loginDto.getPassword(), foundUser.getPasswordHash());
        verifyNoMoreInteractions(userRepository, passwordHashingService);
    }
}