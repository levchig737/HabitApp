package org.habitApp.auth;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.UUID;

@Component
public class JwtUtil {
    private final int expirationTimeInMilliseconds;
    private final Algorithm algorithm;

    public JwtUtil(@Value("${security.jwt.secret}") String jwtSecret, @Value("${security.jwt.expirationTimeInMilliseconds}") int expirationTimeInMilliseconds) {
        this.algorithm = Algorithm.HMAC256(jwtSecret);
        this.expirationTimeInMilliseconds = expirationTimeInMilliseconds;
    }

    public String generate(String email) {
        return JWT.create()
                .withIssuer("Habit Tracker API")
                .withClaim("email", email)
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + expirationTimeInMilliseconds))
                .withJWTId(UUID.randomUUID().toString())
                .sign(algorithm);
    }

    public String verifyAndGetUserId(String token) {
        try {
            JWTVerifier verifier = JWT.require(algorithm).build();
            DecodedJWT decodedJWT = verifier.verify(token);
            return decodedJWT.getClaim("email").asString();
        } catch (JWTVerificationException e) {
            return null;
        }
    }
}
