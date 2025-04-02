package com.volzhin.auction.config.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
public class UserIdFilter extends OncePerRequestFilter {
    private static final ThreadLocal<Long> userIdHolder = new ThreadLocal<>();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        String userIdHeader = request.getHeader("X-User-Id");
        if (userIdHeader != null) {
            try {
                Long userId = Long.valueOf(userIdHeader);
                userIdHolder.set(userId);
            } catch (NumberFormatException e) {
                log.warn("Invalid X-User-Id header: {}", userIdHeader);
            }
        }

        try {
            chain.doFilter(request, response);
        } finally {
            userIdHolder.remove();
        }
    }

    public Long getCurrentUserId() {
        return userIdHolder.get();
    }
}