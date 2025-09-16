package az.ingress.common.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public final class JwtService {
    private SecretKey key;

    @Value("${spring.security.jwt-secret-key}")
    private String jwtSecretKey;


    @PostConstruct
    public void init() {
        byte[] keyBytes;
        keyBytes = Decoders.BASE64.decode(jwtSecretKey);
        key = Keys.hmacShaKeyFor(keyBytes);
    }

    public Jws<Claims> parseToken(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token);
        } catch (Exception e) {
            log.error("Error parsing token", e);
            throw new JwtException("Invalid token");
        }
    }

    public String getUsername(String token) {
        return parseToken(token).getPayload().get("username", String.class);
    }

    public String generateToken(String email, Map<String, Object> extraClaims) {
        return generateToken(extraClaims, email);
    }

    public String generateRefresh(String email, Map<String, Object> extraClaims) {
        return generateToken(extraClaims, email);
    }

    private String generateToken(Map<String, Object> map, String email) {
        long jwtTokenTime = 360000000;
        return Jwts
                .builder()
                .signWith(key)
                .claims()
                .issuedAt(new Date(System.currentTimeMillis()))
                .add(map)
                .subject(email)
                .expiration(new Date(System.currentTimeMillis() + jwtTokenTime))
                .and().compact();
    }
}
