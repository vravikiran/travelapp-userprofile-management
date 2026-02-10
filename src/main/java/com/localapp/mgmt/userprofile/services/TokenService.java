package com.localapp.mgmt.userprofile.services;

import com.localapp.mgmt.userprofile.entities.UserProfile;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.gen.RSAKeyGenerator;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.UUID;

@Service
public class TokenService {
    private static final RSAKey rsaJwk;
    private static final RSAKey publicJwk;

    static {
        try {
            rsaJwk = new RSAKeyGenerator(2048).keyID(UUID.randomUUID().toString()).generate();
            publicJwk = rsaJwk.toPublicJWK();
        } catch (JOSEException e) {
            throw new RuntimeException(e);
        }
    }

    public static String generateToken(String subject, UserProfile userProfile, String tokenType) throws JOSEException {
        JWSSigner signer = new RSASSASigner(rsaJwk);
        JWTClaimsSet claims = new JWTClaimsSet.Builder()
                .subject(subject)
                .issuer("auth-service")
                .claim("token-type", tokenType)
                .claim("role-name", userProfile != null ? userProfile.getRole().getRole_name() : null)
                .claim("role-id", userProfile != null ? userProfile.getRole().getRole_id() : null)
                .expirationTime(new Date(new Date().getTime() + 3600 * 1000)) // 1 hr expiry
                .build();
        JWSHeader header = new JWSHeader.Builder(JWSAlgorithm.RS256)
                .keyID(rsaJwk.getKeyID())
                .type(JOSEObjectType.JWT)
                .build();
        SignedJWT signedJWT = new SignedJWT(header, claims);
        signedJWT.sign(signer);
        return signedJWT.serialize();
    }

    public static String getJWKS() {
        JWKSet jwkSet = new JWKSet(publicJwk);
        return jwkSet.toString();
    }

}