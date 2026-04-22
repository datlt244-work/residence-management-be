package com.base.infra.config.storage;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;

import java.net.URI;

@Configuration
@EnableConfigurationProperties(MinioProperties.class)
@ConditionalOnProperty(name = "minio.enabled", havingValue = "true", matchIfMissing = true)
public class MinioS3ClientConfig {

    @Bean
    public S3Client s3Client(final MinioProperties minioProperties) {
        final String endpoint = minioProperties.getEndpoint() != null ? minioProperties.getEndpoint().strip() : "";
        if (endpoint.isEmpty()) {
            throw new IllegalStateException(
                    "minio.endpoint is required when minio.enabled=true (set MINIO_ENDPOINT or minio.endpoint)");
        }
        final String accessKey =
                minioProperties.getAccessKey() != null ? minioProperties.getAccessKey().strip() : "";
        final String secretKey =
                minioProperties.getSecretKey() != null ? minioProperties.getSecretKey().strip() : "";
        if (accessKey.isEmpty() || secretKey.isEmpty()) {
            throw new IllegalStateException(
                    "minio.access-key and minio.secret-key are required when minio.enabled=true "
                            + "(set MINIO_ACCESS_KEY / MINIO_SECRET_KEY)");
        }

        final Region region = Region.of(minioProperties.getRegion().strip());
        return S3Client.builder()
                .endpointOverride(URI.create(endpoint))
                .region(region)
                .credentialsProvider(
                        StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKey, secretKey)))
                .serviceConfiguration(
                        S3Configuration.builder()
                                .pathStyleAccessEnabled(minioProperties.isPathStyleAccess())
                                .build())
                .build();
    }
}
