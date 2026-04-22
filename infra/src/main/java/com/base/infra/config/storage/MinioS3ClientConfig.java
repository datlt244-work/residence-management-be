package com.base.infra.config.storage;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

import java.net.URI;

@Configuration
@ConditionalOnProperty(name = "minio.enabled", havingValue = "true", matchIfMissing = true)
public class MinioS3ClientConfig {

    @Bean
    public S3Client s3Client(final MinioProperties minioProperties) {
        final MinioClientParams p = MinioClientParams.from(minioProperties);
        return S3Client.builder()
                .endpointOverride(p.endpointUri())
                .region(p.region())
                .credentialsProvider(p.credentials())
                .serviceConfiguration(p.serviceConfiguration())
                .build();
    }

    @Bean
    public S3Presigner s3Presigner(final MinioProperties minioProperties) {
        final MinioClientParams p = MinioClientParams.from(minioProperties);
        return S3Presigner.builder()
                .endpointOverride(p.endpointUri())
                .region(p.region())
                .credentialsProvider(p.credentials())
                .serviceConfiguration(p.serviceConfiguration())
                .build();
    }

    private record MinioClientParams(
            URI endpointUri,
            Region region,
            StaticCredentialsProvider credentials,
            S3Configuration serviceConfiguration) {

        static MinioClientParams from(final MinioProperties minioProperties) {
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
            final var credentials =
                    StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKey, secretKey));
            final var serviceConfiguration =
                    S3Configuration.builder()
                            .pathStyleAccessEnabled(minioProperties.isPathStyleAccess())
                            .build();
            return new MinioClientParams(URI.create(endpoint), region, credentials, serviceConfiguration);
        }
    }
}
