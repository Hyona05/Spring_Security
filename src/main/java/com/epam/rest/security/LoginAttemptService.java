package com.epam.rest.security;

import lombok.Data;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class LoginAttemptService {

    private static final int MAX_ATTEMPTS = 3;
    private static final long BLOCK_DURATION_MINUTES = 5;

    private final Map<String, LoginAttempt> attempts = new ConcurrentHashMap<>();

    public void loginSucceeded(String username) {
        attempts.remove(username);
    }

    public void loginFailed(String username) {
        LoginAttempt attempt = attempts.getOrDefault(username, new LoginAttempt());
        attempt.setCount(attempt.getCount() + 1);
        if (attempt.getCount() >= MAX_ATTEMPTS) {
            attempt.setBlockedUntil(
                    LocalDateTime.now().plusMinutes(BLOCK_DURATION_MINUTES)
            );
        }
        attempts.put(username, attempt);
    }

    public boolean isBlocked(String username) {
        LoginAttempt attempt = attempts.get(username);
        if (attempt == null) return false;
        if (attempt.getBlockedUntil() == null) return false;
        if (LocalDateTime.now().isAfter(attempt.getBlockedUntil())) {
            attempts.remove(username);
            return false;
        }
        return true;
    }

    @Data
    private static class LoginAttempt {
        private int count = 0;
        private LocalDateTime blockedUntil;
    }
}