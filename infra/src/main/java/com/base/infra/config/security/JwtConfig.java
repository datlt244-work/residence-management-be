package com.base.infra.config.security;


import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

@Component
@ConfigurationProperties(prefix = "jwt")
@Getter
@Setter
@Slf4j
public class JwtConfig {
    private Long expiration = 86400000L; // 24 hours
    private String issuer = "java-base-ddd";
    private String audience = "java-base-ddd-users";

    // RSA Key configuration
    private RsaKeys rsa = new RsaKeys();
    @Getter
    @Setter
    public static class RsaKeys {
        private Resource privateKey;
        private Resource publicKey;
        private String privateKeyPem;
        private String publicKeyPem;
    }
    private RSAPrivateKey rsaPrivateKey;
    private RSAPublicKey rsaPublicKey;

    public void setRsaPrivateKey(RSAPrivateKey rsaPrivateKey) {
        this.rsaPrivateKey = rsaPrivateKey;
        log.info("RSA Private Key loaded successfully");
    }

    public void setRsaPublicKey(RSAPublicKey rsaPublicKey) {
        this.rsaPublicKey = rsaPublicKey;
        log.info("RSA Public Key loaded successfully");
    }
}