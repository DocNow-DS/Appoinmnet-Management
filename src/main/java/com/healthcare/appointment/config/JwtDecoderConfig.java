package com.healthcare.appointment.config;

import java.nio.charset.StandardCharsets;

import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtDecoders;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;

@Configuration
public class JwtDecoderConfig {

    @Bean
    JwtDecoder jwtDecoder(
            @Value("${JWT_SECRET:}") String jwtSecret,
            @Value("${JWT_ALGORITHM:HmacSHA256}") String jwtAlgorithm,
            @Value("${JWT_JWK_SET_URI:}") String jwtJwkSetUri,
            @Value("${JWT_ISSUER_URI:}") String jwtIssuerUri) {

        // Supports multiple JWT signing setups:
        // - Symmetric (HS256/HS512/...) via JWT_SECRET
        // - Asymmetric via JWK set URI (JWT_JWK_SET_URI)
        // - Asymmetric via issuer discovery (JWT_ISSUER_URI)
        if (jwtSecret != null && !jwtSecret.isBlank()) {
            var secretKey = new SecretKeySpec(jwtSecret.getBytes(StandardCharsets.UTF_8), jwtAlgorithm);
            return NimbusJwtDecoder.withSecretKey(secretKey).build();
        }

        if (jwtJwkSetUri != null && !jwtJwkSetUri.isBlank()) {
            return NimbusJwtDecoder.withJwkSetUri(jwtJwkSetUri).build();
        }

        if (jwtIssuerUri != null && !jwtIssuerUri.isBlank()) {
            return JwtDecoders.fromIssuerLocation(jwtIssuerUri);
        }

        throw new IllegalStateException(
                "Missing JWT configuration. Provide JWT_SECRET (HS*), or JWT_JWK_SET_URI (JWK), or JWT_ISSUER_URI (issuer discovery).");
    }
}

