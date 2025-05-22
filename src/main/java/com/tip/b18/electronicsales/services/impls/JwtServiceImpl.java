package com.tip.b18.electronicsales.services.impls;

import com.tip.b18.electronicsales.services.JwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.UUID;

@Service
public class JwtServiceImpl implements JwtService {
    private final Key key;

    public JwtServiceImpl(@Value("${jwt.secret}") String SECRET_KEY){
        this.key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
    }

    @Override
    public String generateToken(String user_name, UUID accountId, boolean role) {
        return Jwts.builder()
                .setSubject(user_name)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 86400000))
                .signWith(key, SignatureAlgorithm.HS256)
                .claim("role", role ? "ADMIN" : "USER")
                .claim("id", accountId)
                .compact();
    }

    @Override
    public String getToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if(bearerToken != null && bearerToken.startsWith("Bearer ")){
            return bearerToken.substring(7);
        }
        return null;
    }

    @Override
    public boolean validateToken(String token) {
        try {
            Jwts
                    .parser()
                    .setSigningKey(key)
                    .parseClaimsJws(token);
            return true;
        }catch (JwtException e){
            return false;
        }
    }

    @Override
    public Claims extractClaims(String token) {
        return Jwts
                .parser()
                .setSigningKey(key)
                .parseClaimsJws(token)
                .getBody();
    }
}
