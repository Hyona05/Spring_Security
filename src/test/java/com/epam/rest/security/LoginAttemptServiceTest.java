package com.epam.rest.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("LoginAttemptService Unit Tests")
class LoginAttemptServiceTest {

    private LoginAttemptService service;

    @BeforeEach
    void setUp() {
        service = new LoginAttemptService();
    }

    @Test
    @DisplayName("isBlocked: returns false for new user")
    void isBlocked_newUser_returnsFalse() {
        assertThat(service.isBlocked("John.Doe")).isFalse();
    }

    @Test
    @DisplayName("loginFailed: 1 attempt — user NOT blocked yet")
    void loginFailed_oneAttempt_notBlocked() {
        service.loginFailed("John.Doe");

        assertThat(service.isBlocked("John.Doe")).isFalse();
    }

    @Test
    @DisplayName("loginFailed: 2 attempts — user NOT blocked yet")
    void loginFailed_twoAttempts_notBlocked() {
        service.loginFailed("John.Doe");
        service.loginFailed("John.Doe");

        assertThat(service.isBlocked("John.Doe")).isFalse();
    }

    @Test
    @DisplayName("loginFailed: 3 attempts — user IS blocked")
    void loginFailed_threeAttempts_blocked() {
        service.loginFailed("John.Doe");
        service.loginFailed("John.Doe");
        service.loginFailed("John.Doe");

        assertThat(service.isBlocked("John.Doe")).isTrue();
    }

    @Test
    @DisplayName("loginFailed: 5 attempts — user still blocked")
    void loginFailed_fiveAttempts_stillBlocked() {
        for (int i = 0; i < 5; i++) service.loginFailed("John.Doe");

        assertThat(service.isBlocked("John.Doe")).isTrue();
    }

    @Test
    @DisplayName("loginSucceeded: clears block after 3 failed attempts")
    void loginSucceeded_afterBlock_clearsBlock() {
        service.loginFailed("John.Doe");
        service.loginFailed("John.Doe");
        service.loginFailed("John.Doe");
        assertThat(service.isBlocked("John.Doe")).isTrue();

        service.loginSucceeded("John.Doe");

        assertThat(service.isBlocked("John.Doe")).isFalse();
    }

    @Test
    @DisplayName("loginSucceeded: works for user with no failed attempts")
    void loginSucceeded_noAttempts_noException() {
        service.loginSucceeded("John.Doe");
        assertThat(service.isBlocked("John.Doe")).isFalse();
    }

    @Test
    @DisplayName("isBlocked: different users are tracked independently")
    void isBlocked_differentUsers_independent() {
        service.loginFailed("John.Doe");
        service.loginFailed("John.Doe");
        service.loginFailed("John.Doe");

        assertThat(service.isBlocked("John.Doe")).isTrue();
        assertThat(service.isBlocked("Jane.Doe")).isFalse();
    }
}
