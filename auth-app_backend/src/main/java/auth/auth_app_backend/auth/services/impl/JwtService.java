package auth.auth_app_backend.auth.services.impl;

import auth.auth_app_backend.auth.entities.Role;
import auth.auth_app_backend.auth.entities.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;
@Getter
@Setter
@Service
public class JwtService {

    private final SecretKey key;
    private final long accessTtlSeconds;
    private final long refreshTtlSeconds;
    private final String issuer;

    public JwtService(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.access-ttl-seconds}") long accessTtlSeconds,
            @Value("${jwt.refresh-ttl-seconds}") long refreshTtlSeconds,
            @Value("${jwt.issuer}") String issuer
    ) {

        if (secret == null || secret.length() < 64) {
            throw new IllegalArgumentException(
                    "JWT secret must be at least 64 characters long."
            );
        }

        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.accessTtlSeconds = accessTtlSeconds;
        this.refreshTtlSeconds = refreshTtlSeconds;
        this.issuer = issuer;
    }

    public SecretKey getKey() {
        return key;
    }

    public long getAccessTtlSeconds() {
        return accessTtlSeconds;
    }

    public long getRefreshTtlSeconds() {
        return refreshTtlSeconds;
    }

    public String getIssuer() {
        return issuer;
    }

    // Generate Access Token
    public String generateAccessToken(User user) {

        Instant now = Instant.now();

        List<String> roles = user.getRole() == null
                ? List.of()
                : user.getRole()
                .stream()
                .map(Role::getName)
                .toList();

        return Jwts.builder()
                .id(UUID.randomUUID().toString())
                .subject(user.getUId().toString())
                .issuer(issuer)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusSeconds(accessTtlSeconds)))
                .claims(Map.of(
                        "email", user.getEmail(),
                        "roles", roles,
                        "typ", "access"
                ))
                .signWith(key, Jwts.SIG.HS512)
                .compact();
    }

    // Generate Refresh Token
    public String generateRefreshToken(User user, String jti) {

        Instant now = Instant.now();

        return Jwts.builder()
                .id(jti)
                .subject(user.getUId().toString())
                .issuer(issuer)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusSeconds(refreshTtlSeconds)))
                .claim("typ", "refresh")
                .signWith(key, Jwts.SIG.HS512)
                .compact();
    }

    // Parse Token
    public Jws<Claims> parseToken(String token) {

        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token);

    }

    // Validate Token
    public boolean isTokenValid(String token) {

        try {
            parseToken(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    public boolean isAccessTokenValid(String token) {
        try {
            Claims claims = parseToken(token).getPayload();
            return "access".equals(claims.get("typ"));
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isRefreshTokenValid(String token) {
        try {
            Claims claims = parseToken(token).getPayload();
            return "refresh".equals(claims.get("typ"));
        } catch (Exception e) {
            return false;
        }
    }

    // Get User ID
    public UUID getUserId(String token) {

        Claims claims = parseToken(token).getPayload();

        return UUID.fromString(claims.getSubject());
    }

    // Get Email
    public String getEmail(String token) {

        Claims claims = parseToken(token).getPayload();

        return claims.get("email", String.class);
    }

    // Get Roles
    @SuppressWarnings("unchecked")
    public List<String> getRoles(String token) {

        Claims claims = parseToken(token).getPayload();

        return claims.get("roles", List.class);
    }

    // Get Token Type
    public String getTokenType(String token) {

        Claims claims = parseToken(token).getPayload();

        return claims.get("typ", String.class);
    }
    // Get JTI
    public String getJti(String token) {

        Claims claims = parseToken(token).getPayload();

        return claims.getId();
    }
}