package com.epam.rest.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("JwtService Unit Tests")
class JwtServiceTest {

    private JwtService jwtService;

    private static final String SECRET =
            "bXlTdXBlclNlY3JldEtleUZvckpXVFRva2VuR2VuZXJhdGlvbjEyMzQ1Njc4OTAxMjM0NTY=";
    private static final long EXPIRATION = 86_400_000L;

    private UserDetails userDetails;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        ReflectionTestUtils.setField(jwtService, "secretKey", SECRET);
        ReflectionTestUtils.setField(jwtService, "jwtExpiration", EXPIRATION);

        userDetails = new User(
                "John.Doe", "encoded",
                List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );
    }

    @Test
    @DisplayName("generateToken: returns non-null token with 3 parts")
    void generateToken_returnsValidJwt() {
        String token = jwtService.generateToken(userDetails);

        assertThat(token).isNotNull().isNotBlank();
        assertThat(token.split("\\.")).hasSize(3); // header.payload.signature
    }

    @Test
    @DisplayName("extractUsername: returns correct username from token")
    void extractUsername_returnsCorrectUsername() {
        String token = jwtService.generateToken(userDetails);

        assertThat(jwtService.extractUsername(token)).isEqualTo("John.Doe");
    }

    @Test
    @DisplayName("isTokenValid: returns true for correct user and fresh token")
    void isTokenValid_validToken_returnsTrue() {
        String token = jwtService.generateToken(userDetails);

        assertThat(jwtService.isTokenValid(token, userDetails)).isTrue();
    }

    @Test
    @DisplayName("isTokenValid: returns false when username does not match")
    void isTokenValid_wrongUser_returnsFalse() {
        String token = jwtService.generateToken(userDetails);

        UserDetails otherUser = new User(
                "Jane.Doe", "pass",
                List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );

        assertThat(jwtService.isTokenValid(token, otherUser)).isFalse();
    }

    @Test
    @DisplayName("isTokenValid: returns false for expired token")
    void isTokenValid_expiredToken_returnsFalse() {
        ReflectionTestUtils.setField(jwtService, "jwtExpiration", -1L);
        String expiredToken = jwtService.generateToken(userDetails);

        assertThat(jwtService.isTokenValid(expiredToken, userDetails)).isFalse();
    }

    @Test
    @DisplayName("extractUsername: throws exception for tampered token")
    void extractUsername_tamperedToken_throwsException() {
        String token = jwtService.generateToken(userDetails);
        String tampered = token + "tampered";

        assertThatThrownBy(() -> jwtService.extractUsername(tampered))
                .isInstanceOf(Exception.class);
    }
}
