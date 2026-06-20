package com.linknest.api.security;

import com.linknest.api.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTest {

    private JwtUtil jwtUtil;

    // Must be >= 64 chars for HS512
    private static final String TEST_SECRET =
            "testSecretKeyForUnitTestingOnlyDoNotUseInProductionXYZ0123456789AB";

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
        ReflectionTestUtils.setField(jwtUtil, "secret", TEST_SECRET);
        ReflectionTestUtils.setField(jwtUtil, "expiration", 86400L); // 1 day
    }

    // Test 1: Token is generated and username can be extracted back
    @Test
    void generateToken_ShouldContainCorrectUsername() {
        User user = new User();
        user.setUsername("yajnesh");
        user.setPassword("encodedPassword");
        user.setIsAdmin(false);

        String token = jwtUtil.generateToken(user);

        assertNotNull(token, "Token should not be null");
        assertEquals("yajnesh", jwtUtil.extractUsername(token),
                "Extracted username should match the original");
    }

    // Test 2: Token generated for admin user contains isAdmin = true
    @Test
    void generateToken_AdminUser_ShouldHaveAdminClaim() {
        User adminUser = new User();
        adminUser.setUsername("admin");
        adminUser.setPassword("encodedPassword");
        adminUser.setIsAdmin(true);

        String token = jwtUtil.generateToken(adminUser);

        assertTrue(jwtUtil.extractIsAdmin(token),
                "Admin user's token should have isAdmin = true");
    }

    // Test 3: Token generated for regular user contains isAdmin = false
    @Test
    void generateToken_RegularUser_ShouldNotHaveAdminClaim() {
        User regularUser = new User();
        regularUser.setUsername("regularuser");
        regularUser.setPassword("encodedPassword");
        regularUser.setIsAdmin(false);

        String token = jwtUtil.generateToken(regularUser);

        assertFalse(jwtUtil.extractIsAdmin(token),
                "Regular user's token should have isAdmin = false");
    }

    // Test 4: A freshly generated token should be valid
    @Test
    void validateToken_FreshToken_ShouldReturnTrue() {
        User user = new User();
        user.setUsername("yajnesh");
        user.setPassword("encodedPassword");
        user.setIsAdmin(false);

        String token = jwtUtil.generateToken(user);

        assertTrue(jwtUtil.validateToken(token),
                "A freshly generated token should be valid");
    }

    // Test 5: An expired token should fail validation
    @Test
    void validateToken_ExpiredToken_ShouldThrowException() {
        // Set expiration to -1 second so the token is already expired on creation
        ReflectionTestUtils.setField(jwtUtil, "expiration", -1L);

        User user = new User();
        user.setUsername("yajnesh");
        user.setPassword("encodedPassword");
        user.setIsAdmin(false);

        String expiredToken = jwtUtil.generateToken(user);

        // validateToken calls extractAllClaims which throws on expired token
        assertThrows(Exception.class, () -> jwtUtil.validateToken(expiredToken),
                "Expired token should throw an exception during validation");
    }

    // Test 6: A tampered token should fail validation
    @Test
    void validateToken_TamperedToken_ShouldThrowException() {
        User user = new User();
        user.setUsername("yajnesh");
        user.setPassword("encodedPassword");
        user.setIsAdmin(false);

        String validToken = jwtUtil.generateToken(user);
        // Tamper the signature by changing the last few characters
        String tamperedToken = validToken.substring(0, validToken.length() - 5) + "XXXXX";

        assertThrows(Exception.class, () -> jwtUtil.validateToken(tamperedToken),
                "A tampered token should throw an exception");
    }
}
