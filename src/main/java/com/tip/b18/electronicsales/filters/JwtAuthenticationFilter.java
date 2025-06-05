package com.tip.b18.electronicsales.filters;

import com.tip.b18.electronicsales.exceptions.CredentialsException;
import com.tip.b18.electronicsales.constants.MessageConstant;
import com.tip.b18.electronicsales.services.JwtService;
import com.tip.b18.electronicsales.utils.CookieUtil;
import com.tip.b18.electronicsales.utils.SecurityUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;
import java.io.IOException;
import java.util.Collections;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final HandlerExceptionResolver resolver;

    public JwtAuthenticationFilter(JwtService jwtService, @Qualifier("handlerExceptionResolver") HandlerExceptionResolver resolver) {
        this.jwtService = jwtService;
        this.resolver = resolver;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            String token = CookieUtil.getToken(request);
            if (token == null || !jwtService.validateToken(token)) {
                if(SecurityUtil.isPublicAPI(request)){
                    filterChain.doFilter(request, response);
                    return;
                }
                throw new CredentialsException(MessageConstant.ERROR_INVALID_ACCESS_TOKEN);
            }
            Claims claims = jwtService.extractClaims(token);
            String role = claims.get("role", String.class);
            String id = claims.get("id", String.class);
            SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + role);
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    id, null, Collections.singletonList(authority));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            filterChain.doFilter(request, response);
        }catch (CredentialsException ex){
            resolver.resolveException(request, response, null, ex);
        }
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        if (path.equals("/api/auth/") && request.getMethod().equals("GET")) {
            return false;
        }
        return  path.startsWith("/api/webhook") ||
                path.startsWith("/api/email") ||
                path.startsWith("/login/oauth2/") ||
                path.startsWith("/oauth2/") ||
                path.startsWith("/api/auth/") ||
                path.startsWith("/swagger-ui") ||
                path.startsWith("/v3/api-docs") ||
                path.startsWith("/api/images") ||
                (path.equals("/api/reviews") && request.getMethod().equals("GET"));
    }
}
