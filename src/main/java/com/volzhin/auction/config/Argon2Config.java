package com.volzhin.auction.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;

@Configuration
public class Argon2Config {

    @Value("${spring.password-hash.salt-length}")
    private int saltLength;
    @Value("${spring.password-hash.hash-length}")
    private int hashLength;
    @Value("${spring.password-hash.parallelism}")
    private int parallelism;
    @Value("${spring.password-hash.memory}")
    private int memory;
    @Value("${spring.password-hash.iterations}")
    private int iterations;

    @Bean
    public Argon2PasswordEncoder passwordEncoder() {
        return new Argon2PasswordEncoder(saltLength, hashLength, parallelism, memory, iterations);
    }
}
