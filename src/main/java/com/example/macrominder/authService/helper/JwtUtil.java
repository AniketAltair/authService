package com.example.macrominder.authService.helper;

import com.example.macrominder.authService.enums.AuthType;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {

    private final Key key;

    private static final long ACCESS_TOKEN_EXPIRATION = 2 * 60 * 1000; // 2 minutes
    private static final long REFRESH_TOKEN_EXPIRATION = 7 * 24 * 60 * 60 * 1000; // 30 days

    // Load secret from application.properties or environment variable
    public JwtUtil(@Value("${jwt.secret}") String secretKey) {
        if (secretKey == null || secretKey.isEmpty()) {
            throw new IllegalStateException("JWT secret key is missing!");
        }
        this.key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
        System.out.println("JWT Secret Key Loaded Successfully!");
    }

    public String generateAccessToken(Long userId, String email, String authType) {
        return Jwts.builder()
                .setSubject(email)
                .claim("userId", userId)
                .claim("authType",authType)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN_EXPIRATION))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public String generateRefreshToken(Long userId, String email,String authType) {
        return Jwts.builder()
                .setSubject(email)
                .claim("userId", userId)
                .claim("authType",authType)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + REFRESH_TOKEN_EXPIRATION))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

}
