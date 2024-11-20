package com.honeyosori.dogfile.global.utility;

import com.honeyosori.dogfile.global.constant.PayloadData;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
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

    private final RedisTemplate<String, String> redisTemplate;

    public JwtUtility(@Value("${jwt.secret}") String secret, @Value("${jwt.expiration}") long jwtExpiration, RedisTemplate<String, String> redisTemplate) {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        this.key = Keys.hmacShaKeyFor(keyBytes);
        this.jwtExpiration = jwtExpiration;
        this.redisTemplate = redisTemplate;
    }

    public ResponseEntity<?> generateJwtResponse(Map<String, String> claims) {
        Date now = new Date();

        String accessToken = Jwts.builder()
                .claims(claims)
                .issuedAt(now)
                .expiration(new Date(now.getTime() + jwtExpiration)).signWith(key)
                .compact();

        String refreshToken = Jwts.builder()
                .claims(claims)
                .issuedAt(now)
                .expiration(new Date(now.getTime() + 7 * jwtExpiration)).signWith(key)
                .compact();

        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set(HttpHeaders.AUTHORIZATION, accessToken);

        HttpCookie cookie = new HttpCookie(PayloadData.REFRESH_TOKEN, refreshToken);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        responseHeaders.add(HttpHeaders.SET_COOKIE, cookie.toString().replace("\"", ""));

        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
        valueOperations.set(claims.get(PayloadData.EMAIL), refreshToken);

        return ResponseEntity.ok().headers(responseHeaders).build();
    }
}
