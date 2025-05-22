package com.tip.b18.electronicsales.services;

import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import java.util.UUID;

public interface JwtService {
    String generateToken(String user_name, UUID accountId, boolean role);
    String getToken(HttpServletRequest request);
    boolean validateToken(String token);
    Claims extractClaims(String token);
}
