package com.honeyosori.dogfile.global.utility;

import com.honeyosori.dogfile.domain.user.entity.User;
import com.honeyosori.dogfile.global.response.BaseResponseStatus;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SecurityException;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
@PropertySource(value = "classpath:application-jwt.yml")
public class JwtUtility {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final Key key;
    private final Long jwtExpiration;

    public JwtUtility(@Value("${jwt.secret}") String secret, @Value("${jwt.expiration}") long jwtExpiration) {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        this.key = Keys.hmacShaKeyFor(keyBytes);
        this.jwtExpiration = jwtExpiration;
    }

    public String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");

        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }

        return null;
    }

    public Claims getUserInfoFromToken(String token) {
        return Jwts.parser().verifyWith((SecretKey) key).build().parseSignedClaims(token).getPayload();
    }

    public Map<String, Object> createClaims(String username, String password) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("username", username);
        claims.put("password", password);

        return claims;
    }

    public String generateAccessToken(String username, String password) {
        Date now = new Date();

        return Jwts.builder().claims(createClaims(username, password)).issuedAt(now).expiration(new Date(now.getTime() + jwtExpiration)).signWith(key).compact();
    }

    public BaseResponseStatus validateToken(String token) {
        try {
            Jwts.parser().verifyWith((SecretKey) key).build().parseSignedClaims(token);
            return BaseResponseStatus.SUCCESS;
        } catch (SecurityException | MalformedJwtException | UnsupportedJwtException | IllegalArgumentException e) {
            return BaseResponseStatus.INVALID_JWT_TOKEN;
        } catch (ExpiredJwtException e) {
            return BaseResponseStatus.EXPIRED_JWT_TOKEN;
        }
    }
}
