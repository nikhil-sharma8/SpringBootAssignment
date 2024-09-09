package com.zemoso.User.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import io.jsonwebtoken.ExpiredJwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import io.jsonwebtoken.Jwts;

import java.util.Date;

public class JWTServiceTest {

    @Autowired
    private JWTService jwtService;

    private String testUsername = "testUser";
    private String testToken;
    private UserDetails userDetails;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        jwtService = new JWTService();
        userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn(testUsername);

        testToken = jwtService.generateToken(testUsername);
    }

    @Test
    public void testGenerateToken() {
        assertNotNull(testToken);
        assertTrue(testToken.length() > 0);
    }

    @Test
    public void testExtractUserName() {
        String extractedUsername = jwtService.extractUserName(testToken);
        assertEquals(testUsername, extractedUsername);
    }

    @Test
    public void testValidateToken() {
        boolean isValid = jwtService.validateToken(testToken, userDetails);
        assertTrue(isValid);
    }

    @Test
    public void testIsTokenExpired_False() {
        boolean isExpired = jwtService.isTokenExpired(testToken);
        assertFalse(isExpired);
    }

    @Test
    public void testIsTokenExpired_True() {
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
    public void testExtractExpiration() {
        Date expirationDate = jwtService.extractExpiration(testToken);
        assertNotNull(expirationDate);
        assertTrue(expirationDate.after(new Date()));
    }
}

