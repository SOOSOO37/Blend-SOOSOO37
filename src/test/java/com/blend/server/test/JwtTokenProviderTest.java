package com.blend.server.test;

import com.blend.server.security.jwt.JwtTokenProvider;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.io.Decoders;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class JwtTokenProviderTest {
    private static JwtTokenProvider jwtTokenProvider;
    private String secretKey;
    private String base64EncodedSecretKey;

    @BeforeAll
    public void init() {
        jwtTokenProvider = new JwtTokenProvider();
        secretKey = "secretkey12345678890234546456676768";

        base64EncodedSecretKey = jwtTokenProvider.encodeBase64SecretKey(secretKey);
    }

    @Test
    @DisplayName("secret key 인코딩 테스트")
    public void encodeBase64SecretKeyTest() {

        String expectedEncodedKey = Base64.getEncoder().encodeToString(secretKey.getBytes(StandardCharsets.UTF_8));
        String actualEncodedKey = jwtTokenProvider.encodeBase64SecretKey(secretKey);
        assertEquals(expectedEncodedKey, actualEncodedKey);
    }

    @Test
    @DisplayName("access 토큰 생성 테스트")
    public void generateAccessTokenTest() {
        Map<String, Object> claims = new HashMap<>();
        claims.put("id", 1);
        claims.put("roles", List.of("USER"));

        String subject = "access token";
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, 30);
        Date expiration = calendar.getTime();

        String accessToken
                = jwtTokenProvider.generateAccessToken(claims, subject, expiration, base64EncodedSecretKey);

        System.out.println(accessToken);

        assertThat(accessToken, notNullValue());
    }

    @Test
    @DisplayName("refresh token 생성 테스트")
    public void generateRefreshTokenTest() {
        String subject = "refresh token";
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR, 24);
        Date expiration = calendar.getTime();

        String refreshToken = jwtTokenProvider.generateRefreshToken(subject, expiration, base64EncodedSecretKey);

        System.out.println(refreshToken);

        assertThat(refreshToken, notNullValue());
    }

    @Test
    @DisplayName("Signature 테스트")
    public void verifySignatureTest() {
        String accessToken  = getAccessToken(Calendar.MINUTE, 30);
        assertDoesNotThrow(() -> jwtTokenProvider.verifySignature(accessToken, base64EncodedSecretKey));
    }

    @Test
    @DisplayName("JWT 만료 테스트")
    public void verifyExpirationTest() throws InterruptedException {
        String accessToken = getAccessToken(Calendar.SECOND, 1);
        assertDoesNotThrow(() -> jwtTokenProvider.verifySignature(accessToken, base64EncodedSecretKey));

        TimeUnit.MILLISECONDS.sleep(1500);

        assertThrows(ExpiredJwtException.class,
                () -> jwtTokenProvider.verifySignature(accessToken, base64EncodedSecretKey));
    }

    private String getAccessToken(int timeUnit, int timeAmount) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("id", 1);
        claims.put("roles", List.of("USER"));

        String subject = "test access token";
        Calendar calendar = Calendar.getInstance();
        calendar.add(timeUnit, timeAmount);
        Date expiration = calendar.getTime();
        String accessToken
                = jwtTokenProvider.generateAccessToken(claims, subject, expiration, base64EncodedSecretKey);

        return accessToken;
    }
}
