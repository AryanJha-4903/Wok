package com.Wok.Wok.Services;

import java.time.Instant;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

// import com.Wok.Wok.Model.user;
import com.Wok.Wok.Repository.UserRepository;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {

    private static final String SECRET_KEY = Base64.getEncoder().encodeToString("aryankoutilyajha4903127025262729".getBytes());
    private static final long VALIDITY = TimeUnit.MINUTES.toMillis(1000);

    @Autowired
    private UserRepository userRepository;

    public String generateToken(UserDetails user) {
        System.out.println("UserDetails:" + user);
        Map<String, String> claims = new HashMap<>();
        claims.put("iss", "wok.io");
        claims.put("id", userRepository.findByUsername(user.getUsername()).get().getId());
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(user.getUsername())
                .setIssuedAt(Date.from(Instant.now()))
                .setExpiration(Date.from(Instant.now().plusMillis(VALIDITY)))
                .signWith(generateKey())
                .compact();
    }

    private SecretKey generateKey() {
        return Keys.hmacShaKeyFor(Base64.getDecoder().decode(SECRET_KEY));
    }

    public Claims extractClaims(String token) {
        return Jwts.parserBuilder().setSigningKey(generateKey()).build().parseClaimsJws(token).getBody();
    }

    public boolean validateToken(String token, UserDetails userDetails) {
        String username = extractClaims(token).getSubject();
        return username.equals(userDetails.getUsername()) && extractClaims(token).getExpiration().after(new Date());
    }

    public String extractUsername(String jwtToken) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(generateKey())  // Set the signing key
                .build()
                .parseClaimsJws(jwtToken)     // Parse the JWT
                .getBody();                   // Extract claims
        
        return claims.getSubject();  // Get the username from "sub" claim
    }
}
