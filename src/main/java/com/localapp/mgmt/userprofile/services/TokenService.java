package com.localapp.mgmt.userprofile.services;

import com.localapp.mgmt.userprofile.entities.UserProfile;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class TokenService {

    private RSAKey rsaJwk;
    private RSAKey publicJwk;
    @Autowired
    private SecretsService secretsService;

    @PostConstruct
    public void init() throws Exception {

        String privateKeyPem = secretsService.getPrivateKey();
        RSAKey parsedKey = RSAKey.parseFromPEMEncodedObjects(privateKeyPem).toRSAKey();
        rsaJwk = new RSAKey.Builder(parsedKey.toRSAPublicKey())
                .privateKey(parsedKey.toPrivateKey())
                .keyID("prod-key-1")
                .algorithm(JWSAlgorithm.RS256)
                .build();
        publicJwk = rsaJwk.toPublicJWK();
    }


    public  String generateToken(String subject, UserProfile userProfile, String tokenType) throws JOSEException {
        JWSSigner signer = new RSASSASigner(rsaJwk);
        JWTClaimsSet claims = new JWTClaimsSet.Builder()
                .subject(subject)
                .issuer("auth-service")
                .claim("token-type", tokenType)
                .claim("role-name", userProfile != null ? userProfile.getRole().getRoleName() : null)
                .claim("role-id", userProfile != null ? userProfile.getRole().getRoleId() : null)
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

    public String getJWKS() {
        JWKSet jwkSet = new JWKSet(publicJwk);
        return jwkSet.toString();
    }

}