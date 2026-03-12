package com.citymate.userapi.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitaires pour JwtUtil.
 * Pas de Spring context — instanciation directe via ReflectionTestUtils.
 */
class JwtUtilTest {

    private JwtUtil jwtUtil;

    // Clé >= 32 bytes (256 bits) requise par HMAC-SHA256
    private static final String TEST_SECRET =
            "citymate-test-secret-key-for-junit-tests-must-be-at-least-256-bits!!";
    private static final long ACCESS_EXPIRATION  = 3_600_000L;  // 1 heure
    private static final long REFRESH_EXPIRATION = 86_400_000L; // 24 heures

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
        ReflectionTestUtils.setField(jwtUtil, "jwtSecret",              TEST_SECRET);
        ReflectionTestUtils.setField(jwtUtil, "accessTokenExpiration",  ACCESS_EXPIRATION);
        ReflectionTestUtils.setField(jwtUtil, "refreshTokenExpiration", REFRESH_EXPIRATION);
    }

    // ─────────────────────────────────────────
    // Génération de tokens
    // ─────────────────────────────────────────

    @Test
    void generateAccessToken_returnsNonNullToken() {
        String token = jwtUtil.generateAccessToken("alice");
        assertNotNull(token);
        assertFalse(token.isBlank());
    }

    @Test
    void generateAccessToken_withRole_tokenIsNonNull() {
        String token = jwtUtil.generateAccessToken("alice", "STUDENT");
        assertNotNull(token);
        assertFalse(token.isBlank());
    }

    @Test
    void generateRefreshToken_returnsNonNullToken() {
        String token = jwtUtil.generateRefreshToken("alice");
        assertNotNull(token);
        assertFalse(token.isBlank());
    }

    // ─────────────────────────────────────────
    // Extraction d'informations
    // ─────────────────────────────────────────

    @Test
    void extractUsername_fromAccessToken_returnsCorrectUsername() {
        String token = jwtUtil.generateAccessToken("alice");
        assertEquals("alice", jwtUtil.extractUsername(token));
    }

    @Test
    void extractUsername_fromRefreshToken_returnsCorrectUsername() {
        String token = jwtUtil.generateRefreshToken("bob");
        assertEquals("bob", jwtUtil.extractUsername(token));
    }

    @Test
    void extractRole_withRoleInToken_returnsRole() {
        String token = jwtUtil.generateAccessToken("alice", "ADMIN");
        assertEquals("ADMIN", jwtUtil.extractRole(token));
    }

    @Test
    void extractRole_withoutRole_returnsNull() {
        String token = jwtUtil.generateAccessToken("alice");
        assertNull(jwtUtil.extractRole(token));
    }

    // ─────────────────────────────────────────
    // Validation de tokens
    // ─────────────────────────────────────────

    @Test
    void validateToken_withValidTokenAndCorrectUsername_returnsTrue() {
        String token = jwtUtil.generateAccessToken("alice");
        assertTrue(jwtUtil.validateToken(token, "alice"));
    }

    @Test
    void validateToken_withValidTokenButWrongUsername_returnsFalse() {
        String token = jwtUtil.generateAccessToken("alice");
        assertFalse(jwtUtil.validateToken(token, "bob"));
    }

    @Test
    void validateToken_withInvalidToken_returnsFalse() {
        assertFalse(jwtUtil.validateToken("not.a.valid.token", "alice"));
    }

    // ─────────────────────────────────────────
    // Type de token (access vs refresh)
    // ─────────────────────────────────────────

    @Test
    void isAccessToken_withAccessToken_returnsTrue() {
        String token = jwtUtil.generateAccessToken("alice");
        assertTrue(jwtUtil.isAccessToken(token));
    }

    @Test
    void isAccessToken_withRefreshToken_returnsFalse() {
        String token = jwtUtil.generateRefreshToken("alice");
        assertFalse(jwtUtil.isAccessToken(token));
    }

    @Test
    void isRefreshToken_withRefreshToken_returnsTrue() {
        String token = jwtUtil.generateRefreshToken("alice");
        assertTrue(jwtUtil.isRefreshToken(token));
    }

    @Test
    void isRefreshToken_withAccessToken_returnsFalse() {
        String token = jwtUtil.generateAccessToken("alice");
        assertFalse(jwtUtil.isRefreshToken(token));
    }
}
