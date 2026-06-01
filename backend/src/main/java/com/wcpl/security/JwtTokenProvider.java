package com.wcpl.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtTokenProvider {

    private final SecretKey secretKey;
    private final long accessTokenExpiry;
    private final long refreshTokenExpiry;

    public JwtTokenProvider(
            @Value("${app.jwt.secret}") String secret,
            @Value("${app.jwt.access-token-expiry-ms}") long accessTokenExpiry,
            @Value("${app.jwt.refresh-token-expiry-ms}") long refreshTokenExpiry) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.accessTokenExpiry = accessTokenExpiry;
        this.refreshTokenExpiry = refreshTokenExpiry;
    }

    public String generateAccessToken(Long userId, String role) {
        return buildToken(userId, role, accessTokenExpiry, "access");
    }

    public String generateRefreshToken(Long userId) {
        return buildToken(userId, null, refreshTokenExpiry, "refresh");
    }

    private String buildToken(Long userId, String role, long expiryMs, String type) {
        Date now = new Date();
        JwtBuilder builder = Jwts.builder()
                .subject(String.valueOf(userId))
                .claim("type", type)
                .issuedAt(now)
                .expiration(new Date(now.getTime() + expiryMs))
                .signWith(secretKey);
        if (role != null) {
            builder.claim("role", role);
        }
        return builder.compact();
    }

    public Claims parseToken(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public long getUserId(String token) {
        return Long.parseLong(parseToken(token).getSubject());
    }

    public boolean isAccessToken(String token) {
        return "access".equals(parseToken(token).get("type"));
    }

    public boolean isRefreshToken(String token) {
        return "refresh".equals(parseToken(token).get("type"));
    }

    public boolean validateToken(String token) {
        try {
            parseToken(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
}
