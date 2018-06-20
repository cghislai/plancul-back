package com.charlyghislain.plancul.security;

import com.charlyghislain.plancul.domain.security.ApplicationGroup;
import com.charlyghislain.plancul.domain.security.Caller;
import com.charlyghislain.plancul.security.exception.JwtValidationException;
import org.jose4j.jwa.AlgorithmConstraints;
import org.jose4j.jwk.RsaJsonWebKey;
import org.jose4j.jwk.RsaJwkGenerator;
import org.jose4j.jws.AlgorithmIdentifiers;
import org.jose4j.jws.JsonWebSignature;
import org.jose4j.jwt.JwtClaims;
import org.jose4j.jwt.consumer.InvalidJwtException;
import org.jose4j.jwt.consumer.JwtConsumer;
import org.jose4j.jwt.consumer.JwtConsumerBuilder;
import org.jose4j.lang.JoseException;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import java.util.Set;

@ApplicationScoped
public class JwtService {

    private static final String ISSUER = "PlanCul-WS";
    private static final String AUDIENCE = "PlanCul";
    private static final int EXPIRATION_MINUTES = 10;
    private static final String CALLER_GROUP_CLAIM_KEY = "grps";
    private static final AlgorithmConstraints JWS_ALGORITHM_CONSTRAINTS = new AlgorithmConstraints(
            AlgorithmConstraints.ConstraintType.WHITELIST,
            AlgorithmIdentifiers.RSA_USING_SHA256);


    private RsaJsonWebKey rsaJsonWebKey;
    private JwtConsumer jwtConsumer;


    @PostConstruct
    public void init() {
        try {
            rsaJsonWebKey = RsaJwkGenerator.generateJwk(2048);
            rsaJsonWebKey.setKeyId("k1");
        } catch (JoseException e) {
            throw new RuntimeException("Failed to create rsa key", e);
        }

        jwtConsumer = new JwtConsumerBuilder()
                .setRequireExpirationTime()
                .setAllowedClockSkewInSeconds(30)
                .setRequireSubject()
                .setExpectedIssuer(ISSUER)
                .setExpectedAudience(AUDIENCE)
                .setVerificationKey(rsaJsonWebKey.getKey())
                .setJwsAlgorithmConstraints(JWS_ALGORITHM_CONSTRAINTS)
                .build();
    }

    public String createJwt(Caller caller, Set<ApplicationGroup> callerGroups) {
        JwtClaims claims = new JwtClaims();
        claims.setIssuer(ISSUER);
        claims.setAudience(AUDIENCE);
        claims.setExpirationTimeMinutesInTheFuture(EXPIRATION_MINUTES);
        claims.setGeneratedJwtId();
        claims.setIssuedAtToNow();
        claims.setNotBeforeMinutesInThePast(2);
        claims.setSubject(caller.getName());
        claims.setClaim(CALLER_GROUP_CLAIM_KEY, callerGroups);

        JsonWebSignature jws = new JsonWebSignature();
        jws.setPayload(claims.toJson());
        jws.setKey(rsaJsonWebKey.getPrivateKey());
        jws.setKeyIdHeaderValue(rsaJsonWebKey.getKeyId());
        jws.setAlgorithmHeaderValue(AlgorithmIdentifiers.RSA_USING_SHA256);

        try {
            String jwt = jws.getCompactSerialization();
            return jwt;
        } catch (JoseException e) {
            throw new RuntimeException("Failed to serialize jwt", e);
        }
    }

    public JwtClaims validateToken(String token) throws JwtValidationException {
        try {
            JwtClaims jwtClaims = jwtConsumer.processToClaims(token);
            return jwtClaims;
        } catch (InvalidJwtException e) {
            throw new JwtValidationException(e);
        }

    }
}
