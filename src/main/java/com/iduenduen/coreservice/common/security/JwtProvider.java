package com.iduenduen.coreservice.common.security;

import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtProvider {

    private final SecretKey secretKey;
    private final long accessTokenExpirationMs;
    private final long refreshTokenExpirationMs;

    public JwtProvider(@Value("${jwt.secret}") String secret,
                        @Value("${jwt.access-token-expiration-ms}") long accessTokenExpirationMs,
                        @Value("${jwt.refresh-token-expiration-ms}") long refreshTokenExpirationMs) {
        this.secretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
        this.accessTokenExpirationMs = accessTokenExpirationMs;
        this.refreshTokenExpirationMs = refreshTokenExpirationMs;
    }

    public String createAccessToken(Long parentId) {
        return buildToken(parentId, accessTokenExpirationMs);
    }

    public String createRefreshToken(Long parentId) {
        return buildToken(parentId, refreshTokenExpirationMs);
    }

    public Long getParentId(String token) {
        try {
            String subject = Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload()
                    .getSubject();
            return Long.valueOf(subject);
        } catch (JwtException | IllegalArgumentException e) {
            throw new InvalidTokenException(e);
        }
    }

    public boolean isValid(String token) {
        try {
            Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public long getRemainingExpiration(String token) {
        Date expiration = Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getExpiration();
        return expiration.getTime() - System.currentTimeMillis();
    }

    public long getRefreshTokenExpirationMs() {
        return refreshTokenExpirationMs;
    }

private String buildToken(Long parentId, long expirationMs) {
        Date now = new Date();
        return Jwts.builder()
                .subject(String.valueOf(parentId))
                .issuedAt(now)
                .expiration(new Date(now.getTime() + expirationMs))
                .signWith(secretKey)
                .compact();
    }

    public static class InvalidTokenException extends RuntimeException {
        public InvalidTokenException(Throwable cause) {
            super(cause);
        }
    }
}