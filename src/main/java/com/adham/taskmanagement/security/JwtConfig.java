package com.adham.taskmanagement.security;

import com.nimbusds.jose.jwk.source.ImmutableSecret;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.NoSuchAlgorithmException;

@Configuration
public class JwtConfig {

    @Bean
    public SecretKey jwtSecretKey() throws NoSuchAlgorithmException {

        KeyGenerator keyGenerator =
                KeyGenerator.getInstance("HmacSHA256");

        keyGenerator.init(256);

        return keyGenerator.generateKey();
    }

    @Bean
    public JwtEncoder jwtEncoder(SecretKey secretKey) {

        JWKSource<SecurityContext> secret =
                new ImmutableSecret<>(secretKey);

        return new NimbusJwtEncoder(secret);
    }

    @Bean
    public JwtDecoder jwtDecoder(SecretKey secretKey) {

        return NimbusJwtDecoder
                .withSecretKey(secretKey)
                .macAlgorithm(MacAlgorithm.HS256)
                .build();
    }
}
