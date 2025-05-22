package com.tip.b18.electronicsales.utils;

import com.tip.b18.electronicsales.constants.MessageConstant;
import com.tip.b18.electronicsales.exceptions.CredentialsException;
import com.tip.b18.electronicsales.exceptions.NotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import java.util.UUID;

public class SecurityUtil {
    public static UUID getAuthenticatedUserId(UUID idRequest){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication != null && authentication.getAuthorities() != null){
            String id = authentication.getPrincipal().toString();
            String role = authentication.getAuthorities().iterator().next().getAuthority();

            if ("ROLE_USER".equals(role)) {
                return UUID.fromString(id);
            } else {
                if (idRequest == null) {
                    throw new NotFoundException(MessageConstant.ERROR_VALUE_REQUIRED);
                }
                return idRequest;
            }
        }
        throw new CredentialsException(MessageConstant.ERROR_INVALID_ACCESS_TOKEN);
    }

    public static UUID getAuthenticatedUserId(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication != null && authentication.getAuthorities() != null){
            String id = authentication.getPrincipal().toString();

            return UUID.fromString(id);
        }
        throw new CredentialsException(MessageConstant.ERROR_INVALID_ACCESS_TOKEN);
    }

    public static boolean isAdminRole(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication != null && authentication.getAuthorities() != null){
            String role = authentication.getAuthorities().iterator().next().getAuthority();

            return "ROLE_ADMIN".equals(role);
        }
        return false;
    }

    public static boolean isPublicAPI(HttpServletRequest request){
        String url = request.getRequestURI();
        return ("/api/categories".equals(url) ||
                "/api/brands".equals(url) ||
                url.startsWith("/api/products")) && "GET".equalsIgnoreCase(request.getMethod());
    }
}

