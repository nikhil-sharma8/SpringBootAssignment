package com.zemoso.user.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import io.jsonwebtoken.ExpiredJwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import io.jsonwebtoken.Jwts;

import java.security.NoSuchAlgorithmException;
import java.util.Date;

class JWTServiceTest {

    @Autowired
    private JWTService jwtService;

    String testUsername = "testUser";
    private String testToken;
    private UserDetails userDetails;

    @BeforeEach
    void setUp() throws NoSuchAlgorithmException {
        MockitoAnnotations.openMocks(this);
        jwtService = new JWTService();
        userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn(testUsername);

        testToken = jwtService.generateToken(testUsername);
    }

    @Test
    void testGenerateToken() {
        assertNotNull(testToken);
        assertFalse(testToken.isEmpty());
    }

    @Test
    void testExtractUserName() {
        String extractedUsername = jwtService.extractUserName(testToken);
        assertEquals(testUsername, extractedUsername);
    }

    @Test
    void testValidateToken() {
        boolean isValid = jwtService.validateToken(testToken, userDetails);
        assertTrue(isValid);
    }

    @Test
    void testIsTokenExpired_False() {
        boolean isExpired = jwtService.isTokenExpired(testToken);
        assertFalse(isExpired);
    }

    @Test
    void testIsTokenExpired_True() {
        String expiredToken = Jwts.builder()
                .setSubject("testUser")
                .setIssuedAt(new Date(System.currentTimeMillis() - 10000))
                .setExpiration(new Date(System.currentTimeMillis() - 5000))
                .signWith(jwtService.getKey())
                .compact();

        assertThrows(ExpiredJwtException.class, () -> {
            jwtService.isTokenExpired(expiredToken);
        });
    }

    @Test
    void testExtractExpiration() {
        Date expirationDate = jwtService.extractExpiration(testToken);
        assertNotNull(expirationDate);
        assertTrue(expirationDate.after(new Date()));
    }
}

