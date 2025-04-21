package com.volzhin.auction.service;

import com.volzhin.auction.entity.User;
import com.volzhin.auction.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    @Transactional
    public void save(User user) {
        userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public User getUserById(long id) {
        return userRepository.findById(id).orElseThrow(() -> {
            log.error("User with id {} not found", id);
            return new EntityNotFoundException(String.format("User with id %s not found", id));
        });
    }

    @Transactional(readOnly = true)
    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username).orElseThrow(() -> {
                    log.error("User with name {} not found", username);
                    return new EntityNotFoundException(String.format("User with name %s not found", username));
                });
    }

    @Transactional
    public void decreaseBalance(Long userId, BigDecimal amount) {
        User user = getUserById(userId);
        if (user.getBalance().compareTo(amount) < 0) {
            throw new IllegalArgumentException("Insufficient funds");
        }
        user.setBalance(user.getBalance().subtract(amount));
        userRepository.save(user);
    }

    @Transactional
    public void increaseBalance(Long userId, BigDecimal amount) {
        User user = getUserById(userId);
        user.setBalance(user.getBalance().add(amount));
        userRepository.save(user);
    }

    @Transactional
    public void topUpBalance(long userId, BigDecimal amount) {
        User user = getUserById(userId);
        user.setBalance(user.getBalance().add(amount));

        userRepository.save(user);
    }
}
