package com.example.omni_health_app.util;

import java.util.Collection;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

public class UserNameUtil {


    public static String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated() && !(authentication instanceof AnonymousAuthenticationToken)) {
             Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
            System.out.println("Authenticated User: " + authentication.getName());
            System.out.println("Roles: " + authorities);
            return authentication.getName(); // This returns the username
        }
        return null;
    }
}
