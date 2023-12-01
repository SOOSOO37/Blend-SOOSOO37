package com.blend.server.security.jwt;

import com.blend.server.seller.Seller;
import com.blend.server.user.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.io.Encoders;
import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.*;

@Component
public class JwtTokenProvider {

    @Getter
    @Value("${jwt.key}")
    private String secretKey;

    @Getter
    @Value("${jwt.access-token-expiration-minutes}")
    private int accessTokenExpirationMinutes;

    @Getter
    @Value("${jwt.refresh-token-expiration-minutes}")
    private int refreshTokenExpirationMinutes;

    public String encodeBase64SecretKey(String secretKey) {
        return Encoders.BASE64.encode(secretKey.getBytes(StandardCharsets.UTF_8));
    }

    public String generateAccessToken(Map<String,Object> claims, String subject,
                                      Date expiration, String base64SecretKey) {
        Key key = getKeyFromBase64EncodedKey(base64SecretKey);


        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(Calendar.getInstance().getTime())
                .setExpiration(expiration)
                .signWith(key)
                .compact();
    }

    public String generateAccessToken(User user){
        String base64SecretKey = encodeBase64SecretKey(secretKey);
        Key key = getKeyFromBase64EncodedKey(base64SecretKey);


        Map<String, Object> claims = new HashMap<>();
        claims.put("username", user.getEmail());
        claims.put("roles", user.getRoles());

        String subject = user.getEmail();
        Date expiration = getTokenExpiration(accessTokenExpirationMinutes);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(Calendar.getInstance().getTime())
                .setExpiration(expiration)
                .signWith(key)
                .compact();
    }

    public String generateAccessToken(Seller seller){
        String base64SecretKey = encodeBase64SecretKey(secretKey);
        Key key = getKeyFromBase64EncodedKey(base64SecretKey);


        Map<String, Object> claims = new HashMap<>();
        claims.put("username", seller.getEmail());
        claims.put("roles", seller.getRoles());

        String subject = seller.getEmail();
        Date expiration = getTokenExpiration(accessTokenExpirationMinutes);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(Calendar.getInstance().getTime())
                .setExpiration(expiration)
                .signWith(key)
                .compact();
    }

    public String generateRefreshToken(String subject, Date expiration, String base64EncodedSecretKey) {
        Key key = getKeyFromBase64EncodedKey(base64EncodedSecretKey);


        return Jwts.builder()
                .setSubject(subject)
                .setIssuedAt(Calendar.getInstance().getTime())
                .setExpiration(expiration)
                .signWith(key)
                .compact();
    }

    public String generateRefreshToken(String email) {
        String base64SecretKey = encodeBase64SecretKey(secretKey);
        Key key = getKeyFromBase64EncodedKey(base64SecretKey);

        String subject = email;
        Date expiration = getTokenExpiration(refreshTokenExpirationMinutes);

        return Jwts.builder()
                .setSubject(subject)
                .setIssuedAt(Calendar.getInstance().getTime())
                .setExpiration(expiration)
                .signWith(key)
                .compact();

    }

    public Jws<Claims> getClaims(String jws, String base64SecretKey) {
        Key key = getKeyFromBase64EncodedKey(base64SecretKey);

        Jws<Claims> claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(jws);
        return claims;

    }

    public Date getTokenExpiration(int expirationMinutes) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, expirationMinutes);
        Date expiration = calendar.getTime();

        return expiration;
    }

    public void verifySignature(String jws, String base64EncodedSecretKey) {
        //Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
        Key key = getKeyFromBase64EncodedKey(base64EncodedSecretKey);


        Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(jws);
    }

    private Key getKeyFromBase64EncodedKey(String base64SecretKey) {
        byte[] keyBytes = Decoders.BASE64.decode(base64SecretKey);
        Key key = Keys.hmacShaKeyFor(keyBytes);

        return key;
    }

}

