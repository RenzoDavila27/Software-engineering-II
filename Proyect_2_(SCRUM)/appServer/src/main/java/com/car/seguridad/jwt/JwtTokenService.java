package com.car.seguridad.jwt;

import com.car.seguridad.authentication.UserPrincipal;
import com.car.seguridad.config.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import javax.crypto.SecretKey;
import lombok.Getter;
import org.springframework.stereotype.Service;

@Service
public class JwtTokenService {

    private static final String CLAIM_USERNAME = "username";
    private static final String CLAIM_ROLES = "roles";
    private static final String CLAIM_TOKEN_TYPE = "token_type";

    private final JwtProperties properties;
    private final Clock clock;
    private final SecretKey secretKey;

    @Getter
    public enum TokenType {
        ACCESS,
        REFRESH
    }

    public JwtTokenService(JwtProperties properties, Clock clock) {
        this.properties = properties;
        this.clock = clock;
        this.secretKey = buildSecretKey(properties.getSecret());
    }

    public String generateAccessToken(UserPrincipal principal) {
        return generateToken(principal, TokenType.ACCESS, properties.getAccessTokenValiditySeconds());
    }

    public String generateRefreshToken(UserPrincipal principal) {
        return generateToken(principal, TokenType.REFRESH, properties.getRefreshTokenValiditySeconds());
    }

    public boolean isAccessTokenValid(String token) {
        return isTokenValid(token, TokenType.ACCESS);
    }

    public boolean isRefreshTokenValid(String token) {
        return isTokenValid(token, TokenType.REFRESH);
    }

    public Optional<String> extractUserId(String token) {
        return extractClaims(token).map(Claims::getSubject);
    }

    public Optional<String> extractUsername(String token) {
        return extractClaims(token).map(claims -> claims.get(CLAIM_USERNAME, String.class));
    }

    public List<String> extractRoles(String token) {
        return extractClaims(token)
                .map(claims -> claims.get(CLAIM_ROLES, List.class))
                .map(list -> list.stream().map(Object::toString).toList())
                .orElse(List.of());
    }

    public long getAccessTokenValiditySeconds() {
        return properties.getAccessTokenValiditySeconds();
    }

    public long getRefreshTokenValiditySeconds() {
        return properties.getRefreshTokenValiditySeconds();
    }

    private String generateToken(UserPrincipal principal, TokenType tokenType, long validitySeconds) {
        Instant now = Instant.now(clock);
        Instant expiration = now.plusSeconds(validitySeconds);

        return Jwts.builder()
                .subject(principal.getId())
                .issuer(properties.getIssuer())
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiration))
                .claim(CLAIM_USERNAME, principal.getUsername())
                .claim(CLAIM_ROLES, principal.getAuthorities().stream().map(authority -> authority.getAuthority()).toList())
                .claim(CLAIM_TOKEN_TYPE, tokenType.name())
                .signWith(secretKey)
                .compact();
    }

    private boolean isTokenValid(String token, TokenType expectedTokenType) {
        return extractClaims(token)
                .filter(claims -> expectedTokenType.name().equals(claims.get(CLAIM_TOKEN_TYPE, String.class)))
                .isPresent();
    }

    private Optional<Claims> extractClaims(String token) {
        try {
            Jws<Claims> claims = Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token);
            return Optional.of(claims.getPayload());
        } catch (JwtException | IllegalArgumentException ex) {
            return Optional.empty();
        }
    }

    private SecretKey buildSecretKey(String secret) {
        byte[] keyBytes;
        if (secret.length() % 4 == 0 && secret.matches("[A-Za-z0-9+/=]+")) {
            try {
                keyBytes = Decoders.BASE64.decode(secret);
            } catch (IllegalArgumentException ex) {
                keyBytes = secret.getBytes(StandardCharsets.UTF_8);
            }
        } else {
            keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        }
        if (keyBytes.length < 32) {
            throw new IllegalArgumentException("La clave JWT debe tener al menos 256 bits (32 bytes).");
        }
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
