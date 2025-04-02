package com.volzhin.auction.config.filter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserContext {
    private final UserIdFilter userIdFilter;

    public Long getCurrentUserId() {
        Long userId = userIdFilter.getCurrentUserId();
        if (userId == null) {
            throw new IllegalStateException("No userId provided in request");
        }
        return userId;
    }
}