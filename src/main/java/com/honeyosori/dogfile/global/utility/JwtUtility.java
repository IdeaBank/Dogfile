package com.honeyosori.dogfile.global.utility;

import com.honeyosori.dogfile.global.constant.PayloadData;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.net.HttpCookie;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
@PropertySource(value = "classpath:application-jwt.yml")
public class JwtUtility {
    private final Key key;
    private final Long jwtExpiration;

    public JwtUtility(@Value("${jwt.secret}") String secret, @Value("${jwt.expiration}") long jwtExpiration) {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        this.key = Keys.hmacShaKeyFor(keyBytes);
        this.jwtExpiration = jwtExpiration;
    }

    public ResponseEntity<?> generateJwtResponse(Map<String, String> claims) {
        Date now = new Date();

        String accessToken = Jwts.builder()
                .claims(claims)
                .issuedAt(now)
                .expiration(new Date(now.getTime() + jwtExpiration)).signWith(key)
                .compact();

        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set(HttpHeaders.AUTHORIZATION, accessToken);

        HttpCookie cookie = new HttpCookie(PayloadData.ACCESS_TOKEN, accessToken);
        cookie.setPath("/");

        responseHeaders.add(HttpHeaders.SET_COOKIE, cookie.toString().replace("\"", ""));

        return ResponseEntity.ok().headers(responseHeaders).build();
    }
}
