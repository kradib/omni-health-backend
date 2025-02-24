package com.example.omni_health_app.util;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import com.example.omni_health_app.domain.entity.UserAuth;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;



@Component
public class TokenUtil {

    @Value("${omni.auth.secret}")
    private String secret;

    @Value("${omni.auth.expiration}")
    private long expiration;


    public String generateToken(String username,UserAuth userAuth) {
        //  List<String> roles = List.of(userAuth.getRoles());

        List<String> roles = List.of(userAuth.getRoles()).stream()
                        .map(role -> "ROLE_" + role.toUpperCase()) 
                        .collect(Collectors.toList()
                        );

        return Jwts.builder()
                .setSubject(username)
                .claim("roles", roles)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(SignatureAlgorithm.HS512, secret)
                .compact();
    }

    public boolean validateToken(String token, String username) {
        String tokenUsername = Jwts.parser()
                .setSigningKey(secret)
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
        return username.equals(tokenUsername);
    }
}
