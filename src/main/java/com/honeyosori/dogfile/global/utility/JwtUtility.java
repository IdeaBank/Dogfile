package com.honeyosori.dogfile.global.utility;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@PropertySource("classpath:application.yml")
@Component
public class JwtUtility {
    private final Key key;
    private final Long jwtExpiration;

    public JwtUtility(@Value("${jwt.secret}") String secret, @Value("${jwt.expiration}") long jwtExpiration) {
        //TODO: fix error: TEMP_SECRET
        secret = "iHixb3jBQEqLIJCQvWgMnQKianUU50A7LcAf1gyQWHA";

        byte[] keyBytes = Decoders.BASE64.decode(secret);
        this.key = Keys.hmacShaKeyFor(keyBytes);
        this.jwtExpiration = jwtExpiration;
    }

    public String generateAccessToken(String username) {
        Date now = new Date();

        return Jwts.builder()
                .subject(username)
                .issuedAt(now)
                .expiration(new Date(now.getTime() + jwtExpiration))
                .signWith(key)
                .compact();
    }
}
