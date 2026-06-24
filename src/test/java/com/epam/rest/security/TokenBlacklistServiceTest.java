package com.epam.rest.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("TokenBlacklistService Unit Tests")
class TokenBlacklistServiceTest {

    private TokenBlacklistService service;

    @BeforeEach
    void setUp() {
        service = new TokenBlacklistService();
    }

    @Test
    @DisplayName("isBlacklisted: returns false for unknown token")
    void isBlacklisted_unknownToken_returnsFalse() {
        assertThat(service.isBlacklisted("some.jwt.token")).isFalse();
    }

    @Test
    @DisplayName("blacklist + isBlacklisted: returns true after blacklisting")
    void blacklist_then_isBlacklisted_returnsTrue() {
        service.blacklist("some.jwt.token");

        assertThat(service.isBlacklisted("some.jwt.token")).isTrue();
    }

    @Test
    @DisplayName("blacklist: multiple tokens tracked independently")
    void blacklist_multipleTokens_independentTracking() {
        service.blacklist("token.one");

        assertThat(service.isBlacklisted("token.one")).isTrue();
        assertThat(service.isBlacklisted("token.two")).isFalse();
    }

    @Test
    @DisplayName("blacklist: same token twice — no error, still blacklisted")
    void blacklist_sameTokenTwice_noError() {
        service.blacklist("token.one");
        service.blacklist("token.one"); // duplicate

        assertThat(service.isBlacklisted("token.one")).isTrue();
    }
}
