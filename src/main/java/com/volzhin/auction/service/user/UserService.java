package com.volzhin.auction.service.user;

import com.volzhin.auction.entity.user.User;
import com.volzhin.auction.repository.UserRepository;
import com.volzhin.auction.security.JwtTokenProvider;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;

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

    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username).orElseThrow(() -> {
                    log.error("User with name {} not found", username);
                    return new EntityNotFoundException(String.format("User with name %s not found", username));
                });
    }

    public User getProfile(String bearerToken) {
        String token = bearerToken.replaceFirst("^Bearer\\s+", "");
        String username = jwtTokenProvider.getUsernameFromToken(token);

        return getUserByUsername(username);
    }
}
