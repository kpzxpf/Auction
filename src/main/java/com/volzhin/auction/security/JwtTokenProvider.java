package com.volzhin.auction.security;

import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Base64;
import java.util.Date;

@Slf4j
@Component
public class JwtTokenProvider {
    @Value("${spring.jwt.secret}")
    private String secretKey;
    @Value("${spring.jwt.expiration}")
    private long expirationTime;

    private Key signingKey;
    private JwtParser jwtParser;

    @PostConstruct
    public void init() {
        log.info("Initializing JWT Token Provider");
        byte[] keyBytes = Base64.getDecoder().decode(secretKey);
        this.signingKey = Keys.hmacShaKeyFor(keyBytes);
        this.jwtParser = Jwts.parser().verifyWith((SecretKey) signingKey).build();
    }

    public String generateToken(String username) {
        String token = Jwts.builder()
                .subject(username)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(signingKey)
                .compact();

        log.info("Token successfully generated for the user: {}", username);
        return token;
    }

    public String getUsernameFromToken(String token) {
        try {
            String username = jwtParser.parseSignedClaims(token).getPayload().getSubject();
            log.info("Username '{}' extracted from the token", username);
            return username;
        } catch (Exception e) {
            log.error("Error extracting username from token: {}", e.getMessage());
            throw new RuntimeException("Token parsing error");
        }
    }

    public boolean validateToken(String token) {
        try {
            jwtParser.parseSignedClaims(token);
            log.info("The token is valid");
            return true;
        } catch (Exception e) {
            log.warn("Invalid token: {}", e.getMessage());
            return false;
        }
    }
}
