package com.example.student_management.security;

import com.example.student_management.entity.Student;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JWTService {

    @Value("${jwt.secret}")
    private String secret;
    @Value("${jwt.expiration}")
    private Long expiration;

    public String extractUsername(String token){
        return extractClaim(token, Claims::getSubject);
    }

    public Long extractId(String token){
        return extractAllClaims(token).get("id", Long.class);
    }

    public String extractName(String token){
        return extractAllClaims(token).get("name", String.class);
    }

    public boolean extractIsAdmin(String token){
        return extractAllClaims(token).get("isAdmin", Boolean.class);
    }

    //get the payload that includes all the data or claims
    private Claims extractAllClaims(String token){
        return Jwts.parser()
                .setSigningKey(getSigningKey())
                .build()
                .parseSignedClaims(token)//parse the claims with key
                .getPayload(); //get payload
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver){
        final Claims claims = extractAllClaims(token); //get all the claims from payload in token
        return claimsResolver.apply(claims); //apply Claims method of choice to the claims object
    }

    //get key for signature
    private Key getSigningKey(){
        //decode secret in base 64
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    //added in the custom claims of things we'd like to see in token
    public String generateToken(Student student) {

        Map<String, Object> claims = new HashMap<>();

        claims.put("id", student.getId());
        claims.put("name", student.getName());
        claims.put("isAdmin", student.isAdmin());

        return Jwts.builder()
                .claims(claims)
                .subject(student.getEmail())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSigningKey())
                .compact();
    }

    //see if the token belongs to the user details
    public boolean validateToken(String token, UserDetails userDetails){
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }
}
