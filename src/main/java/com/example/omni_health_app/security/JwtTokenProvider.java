package com.example.omni_health_app.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;


@Component
public class JwtTokenProvider {

    @Value("${omni.auth.secret}")
    private String secret;

    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(secret).parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public String getUsernameFromToken(String token) {
        Claims claims = Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
        return claims.getSubject();
    }

     public List<String> getRolesFromToken(String token) {
         Claims claims = Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
    return claims.get("roles", List.class);
    
    //     Claims claims = Jwts.parser().setSigningKey(secret).
    //     parseClaimsJws(token).getBody();
    //    return roles.stream().map(roles -> "ROLE_" + role.toUpperCase()).collect(Collectors.toList()); 
    }
}
