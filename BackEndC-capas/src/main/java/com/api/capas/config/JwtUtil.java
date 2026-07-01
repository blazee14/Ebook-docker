package com.api.capas.config;

import com.api.capas.infrastructure.persistence.entities.Usuario;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.lang.NonNull;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class JwtUtil {

    private final SecretKey secretKey;

    public JwtUtil() {
        this.secretKey = Keys.hmacShaKeyFor(JwtConstants.SECRET_KEY.getBytes());
    }

    public String generateToken(@NonNull UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();

        if (userDetails instanceof Usuario) {
            Usuario usuario = (Usuario) userDetails;
            claims.put("id", usuario.getId());
            claims.put("nombres", usuario.getNombres());
            claims.put("apellidos", usuario.getApellidos());
            claims.put("roles", userDetails.getAuthorities().stream()
                                            .map(authority -> authority.getAuthority())
                                            .collect(Collectors.toList()));
        }

        return createToken(claims, Objects.requireNonNull(userDetails.getUsername(), "username must not be null"));
    }

    private String createToken(@NonNull Map<String, Object> claims, @NonNull String subject) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + JwtConstants.EXPIRATION_TIME))
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    public Boolean validateToken(@NonNull String token, @NonNull UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    public String extractUsername(@NonNull String token) {
        return extractClaim(token, claims -> claims.getSubject());
    }

    public Date extractExpiration(@NonNull String token) {
        return extractClaim(token, claims -> claims.getExpiration());
    }

    private <T> T extractClaim(@NonNull String token, @NonNull Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(Objects.requireNonNull(claims, "claims must not be null"));
    }

    private Claims extractAllClaims(@NonNull String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Boolean isTokenExpired(@NonNull String token) {
        return extractExpiration(token).before(new Date());
    }
}