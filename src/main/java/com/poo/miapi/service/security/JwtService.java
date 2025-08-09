package com.poo.miapi.service.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;

import com.poo.miapi.model.core.Usuario;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String jwtSecret;

    private long jwtExpirationMs = 1000 * 60 * 60 * 10; // 10 horas

    private Key getSigningKey() {
        if (jwtSecret == null || jwtSecret.length() < 32) {
            throw new IllegalArgumentException("JWT_SECRET debe tener al menos 32 caracteres");
        }
        return Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }

    public String generateToken(Usuario usuario) {
        return Jwts.builder()
                .setSubject(usuario.getEmail())
                .claim("id", usuario.getId())
                .claim("rol", usuario.getTipoUsuario())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // Nuevo: para el filtro, extrae el "username" (email)
    public String extractUsername(String token) {
        return extractEmail(token);
    }

    public String extractEmail(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    // Nuevo: para el filtro, valida el token con UserDetails
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    // Para compatibilidad con tu lógica anterior
    public boolean validateToken(String token, Usuario usuario) {
        final String email = extractEmail(token);
        return (email.equals(usuario.getEmail()) && !isTokenExpired(token));
    }

    private boolean isTokenExpired(String token) {
        Date expiration = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getExpiration();
        return expiration.before(new Date());
    }

    // Para el filtro: genera el objeto de autenticación
public UsernamePasswordAuthenticationToken getAuthentication(String token, UserDetails userDetails) {
    String rol = extractRole(token); // nuevo método que vamos a hacer
    var authorities = java.util.List.of(
        new org.springframework.security.core.authority.SimpleGrantedAuthority("ROLE_" + rol)
    );
    return new UsernamePasswordAuthenticationToken(userDetails, null, authorities);
}

// Nuevo método para extraer el rol del token
public String extractRole(String token) {
    return Jwts.parserBuilder()
            .setSigningKey(getSigningKey())
            .build()
            .parseClaimsJws(token)
            .getBody()
            .get("rol", String.class);
}
}
