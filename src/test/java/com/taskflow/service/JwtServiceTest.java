package com.taskflow.service;

import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class JwtServiceTest {

    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService("unit-test-secret-key-with-at-least-32-characters!", 60_000);
    }

    @Test
    void tokenCarriesUsernameAndRole() {
        String token = jwtService.generateToken("alice", "ADMIN");

        assertEquals("alice", jwtService.extractUsername(token));
        assertEquals("ADMIN", jwtService.extractRole(token));
    }

    @Test
    void tamperedTokenIsRejected() {
        String token = jwtService.generateToken("alice", "USER");
        String tampered = token.substring(0, token.length() - 3) + "abc";

        assertThrows(JwtException.class, () -> jwtService.extractUsername(tampered));
    }

    @Test
    void tokenSignedWithDifferentKeyIsRejected() {
        JwtService otherService = new JwtService("a-completely-different-secret-key-32-chars-long!", 60_000);
        String foreignToken = otherService.generateToken("alice", "ADMIN");

        assertThrows(JwtException.class, () -> jwtService.extractUsername(foreignToken));
    }
}
