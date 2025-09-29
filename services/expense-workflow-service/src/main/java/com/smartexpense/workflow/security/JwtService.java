package com.smartexpense.workflow.security;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.sql.Date;
import java.time.Instant;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;

@Service
public class JwtService {

    private final Key key;

    public JwtService(
            @Value("${app.jwt.secret:default-secret-change-me-0123456789abcdef0123456789abcdef0123456789abcdef}") 
            String secret) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public UsernamePasswordAuthenticationToken buildAuthentication(String token, HttpServletRequest req) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();

        String user = claims.getSubject();
        if (user == null) return null;

        @SuppressWarnings("unchecked")
        List<String> roles = (List<String>) claims.getOrDefault("roles", List.of("ROLE_USER"));
        var auths = roles.stream().map(SimpleGrantedAuthority::new).toList();

        return new UsernamePasswordAuthenticationToken(user, null, auths);
    }
    public String generate(UserDetails user) {
        Instant now = Instant.now();
        Instant exp = now.plusSeconds(60L * 60L); // one hour

        var roles = user.getAuthorities().stream().map(Object::toString).toList();

        return Jwts.builder()
                .setSubject(user.getUsername())               // ✅ use setSubject
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(exp))
                .claim("roles", roles)                        // ✅ use claim instead of claims(Map)
                .signWith(key)
                .compact();
    }

}
