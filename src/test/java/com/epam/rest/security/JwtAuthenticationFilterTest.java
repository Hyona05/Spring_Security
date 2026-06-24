package com.epam.rest.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("JwtAuthenticationFilter Unit Tests")
class JwtAuthenticationFilterTest {

    @Mock JwtService jwtService;
    @Mock UserDetailsService userDetailsService;
    @Mock TokenBlacklistService tokenBlacklistService;
    @InjectMocks JwtAuthenticationFilter filter;

    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    private MockFilterChain chain;
    private UserDetails userDetails;

    @BeforeEach
    void setUp() {
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        chain = new MockFilterChain();
        SecurityContextHolder.clearContext();

        userDetails = new User("John.Doe", "encoded",
                List.of(new SimpleGrantedAuthority("ROLE_USER")));
    }

    @Test
    @DisplayName("No Authorization header — filter passes through, no auth set")
    void noAuthHeader_passesThrough() throws Exception {
        filter.doFilterInternal(request, response, chain);

        assertThat(response.getStatus()).isEqualTo(200);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        verify(jwtService, never()).extractUsername(org.mockito.ArgumentMatchers.any());
    }

    @Test
    @DisplayName("Non-Bearer Authorization header — filter passes through")
    void basicAuthHeader_passesThrough() throws Exception {
        request.addHeader("Authorization", "Basic dXNlcjpwYXNz");

        filter.doFilterInternal(request, response, chain);

        assertThat(response.getStatus()).isEqualTo(200);
        verify(jwtService, never()).extractUsername(org.mockito.ArgumentMatchers.any());
    }

    @Test
    @DisplayName("Blacklisted token — returns 401 without proceeding")
    void blacklistedToken_returns401() throws Exception {
        request.addHeader("Authorization", "Bearer blacklisted.jwt.token");
        given(tokenBlacklistService.isBlacklisted("blacklisted.jwt.token")).willReturn(true);

        filter.doFilterInternal(request, response, chain);

        assertThat(response.getStatus()).isEqualTo(401);
        assertThat(response.getContentAsString()).contains("invalidated");
        verify(jwtService, never()).extractUsername(org.mockito.ArgumentMatchers.any());
    }

    @Test
    @DisplayName("Valid Bearer token — sets authentication in SecurityContext")
    void validToken_setsAuthentication() throws Exception {
        String token = "valid.jwt.token";
        request.addHeader("Authorization", "Bearer " + token);

        given(tokenBlacklistService.isBlacklisted(token)).willReturn(false);
        given(jwtService.extractUsername(token)).willReturn("John.Doe");
        given(userDetailsService.loadUserByUsername("John.Doe")).willReturn(userDetails);
        given(jwtService.isTokenValid(token, userDetails)).willReturn(true);

        filter.doFilterInternal(request, response, chain);

        assertThat(response.getStatus()).isEqualTo(200);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNotNull();
        assertThat(SecurityContextHolder.getContext().getAuthentication().getName())
                .isEqualTo("John.Doe");
    }

    @Test
    @DisplayName("Invalid token (expired/tampered) — passes through but no auth set")
    void invalidToken_noAuthSet() throws Exception {
        String token = "invalid.jwt.token";
        request.addHeader("Authorization", "Bearer " + token);

        given(tokenBlacklistService.isBlacklisted(token)).willReturn(false);
        given(jwtService.extractUsername(token)).willReturn("John.Doe");
        given(userDetailsService.loadUserByUsername("John.Doe")).willReturn(userDetails);
        given(jwtService.isTokenValid(token, userDetails)).willReturn(false);

        filter.doFilterInternal(request, response, chain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }
}
