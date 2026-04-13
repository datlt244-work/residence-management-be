package com.base.infra.config.security.service;

import com.base.domain.employee.domain.Employee;
import com.base.infra.config.security.JwtConfig;
import com.base.infra.config.security.TokenInfo;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
@Slf4j
public class JwtService {

    private final JwtConfig jwtConfig;

    public String generateToken(Employee employee) {
        return generateToken(employee, new HashMap<>());
    }

    public String generateToken(Employee employee, Map<String, Object> extraClaims) {
        return buildToken(employee, extraClaims, jwtConfig.getExpiration());
    }

    public String generateRefreshToken(Employee employee) {
        return buildToken(employee, new HashMap<>(), jwtConfig.getExpiration() * 7);
    }

    private String buildToken(Employee employee, Map<String, Object> extraClaims, Long expiration) {
        Instant now = Instant.now();
        Instant expirationTime = now.plusMillis(expiration);

        Map<String, Object> claims = new HashMap<>(extraClaims);
        claims.put("employeeId", employee.getId());
        claims.put("role", employee.getRole().name());
        claims.put("authorities", employee.getAuthorities());

        RSAPrivateKey privateKey = jwtConfig.getRsaPrivateKey();
        if (privateKey == null) {
            throw new IllegalStateException("RSA Private Key not configured");
        }

        try {
            return Jwts.builder()
                    .setClaims(claims)
                    .setSubject(employee.getId())
                    .setIssuer(jwtConfig.getIssuer())
                    .setAudience(jwtConfig.getAudience())
                    .setIssuedAt(Date.from(now))
                    .setExpiration(Date.from(expirationTime))
                    .signWith(privateKey, SignatureAlgorithm.RS256)
                    .compact();
        } catch (Exception e) {
            log.error("Error generating JWT token", e);
            throw new RuntimeException("Failed to generate JWT token", e);
        }
    }

    public boolean isTokenValid(String token, Employee employee) {
        try {
            final String tokenEmployeeId = extractEmployeeId(token);
            return (tokenEmployeeId != null && tokenEmployeeId.equals(employee.getId())) && !isTokenExpired(token);
        } catch (Exception e) {
            log.warn("Token validation failed: {}", e.getMessage());
            return false;
        }
    }

    public boolean isTokenExpired(String token) {
        try {
            return extractExpiration(token).before(new Date());
        } catch (Exception e) {
            log.warn("Error checking token expiration: {}", e.getMessage());
            return true;
        }
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public String extractEmployeeId(String token) {
        String fromClaim = extractClaim(token, claims -> claims.get("employeeId", String.class));
        if (fromClaim != null) {
            return fromClaim;
        }
        return extractClaim(token, Claims::getSubject);
    }

    public String extractRole(String token) {
        return extractClaim(token, claims -> claims.get("role", String.class));
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        RSAPublicKey publicKey = jwtConfig.getRsaPublicKey();
        if (publicKey == null) {
            throw new IllegalStateException("RSA Public Key not configured");
        }

        try {
            return Jwts.parserBuilder()
                    .setSigningKey(publicKey)
                    .requireIssuer(jwtConfig.getIssuer())
                    .requireAudience(jwtConfig.getAudience())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (JwtException e) {
            log.warn("JWT parsing failed: {}", e.getMessage());
            throw new RuntimeException("Invalid JWT token", e);
        }
    }

    public boolean validateToken(String token, Employee employee) {
        try {
            return isTokenValid(token, employee);
        } catch (Exception e) {
            log.warn("Token validation error: {}", e.getMessage());
            return false;
        }
    }

    public boolean validateTokenStructure(String token) {
        try {
            extractAllClaims(token);
            return true;
        } catch (Exception e) {
            log.warn("Token structure validation failed: {}", e.getMessage());
            return false;
        }
    }

    public Long getExpirationTime() {
        return jwtConfig.getExpiration();
    }

    public String getIssuer() {
        return jwtConfig.getIssuer();
    }

    public String getAudience() {
        return jwtConfig.getAudience();
    }

    public TokenInfo extractTokenInfo(String token) {
        try {
            Claims claims = extractAllClaims(token);
            return TokenInfo.builder()
                    .username(claims.getSubject())
                    .employeeId(claims.get("employeeId", String.class))
                    .role(claims.get("role", String.class))
                    .issuer(claims.getIssuer())
                    .audience(claims.getAudience())
                    .issuedAt(claims.getIssuedAt())
                    .expiresAt(claims.getExpiration())
                    .build();
        } catch (Exception e) {
            log.warn("Error extracting token info: {}", e.getMessage());
            throw new RuntimeException("Failed to extract token information", e);
        }
    }
}
