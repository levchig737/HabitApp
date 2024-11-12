package org.habitApp.auth;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.habitApp.utils.YamlPropertySourceFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.UUID;


@Component
@PropertySource(value = "classpath:application.yml", factory = YamlPropertySourceFactory.class)
public class JwtUtil {
    private final int expirationTimeInMilliseconds;
    private final Algorithm algorithm;

    @Autowired
    public JwtUtil(@Value("${security.jwt.secret}") String jwtSecret, @Value("${security.jwt.expirationTimeInMilliseconds}") int expirationTimeInMilliseconds) {
        algorithm = Algorithm.HMAC256(jwtSecret);
        this.expirationTimeInMilliseconds = expirationTimeInMilliseconds;
    }

    public String generate(Long userId) {
        return JWT.create()
                .withIssuer("Habit Tracker API")
                .withSubject("Habit Tracker Client")
                .withClaim("userId", userId)
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + expirationTimeInMilliseconds))
                .withJWTId(UUID.randomUUID()
                        .toString())
                .sign(algorithm);
    }

    public Long verifyAndGetUserId(String token) {
        try {
            JWTVerifier verifier = JWT.require(algorithm).build();
            DecodedJWT decodedJWT = verifier.verify(token);
            return decodedJWT.getClaim("userId").asLong();
        } catch (JWTVerificationException e) {
            return null;
        }
    }
}
