package com.base.infra.config.storage;

import com.base.domain.apartment.ApartmentMediaUrlSigning;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

import java.net.URI;
import java.time.Duration;
import java.util.Arrays;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class S3ApartmentMediaUrlSigning implements ApartmentMediaUrlSigning {

    private final MinioProperties minioProperties;
    private final ObjectProvider<S3Presigner> presignerProvider;

    @Override
    public String presignGetUrl(final String storedUrlOrKey) {
        if (storedUrlOrKey == null || storedUrlOrKey.isBlank()) {
            return storedUrlOrKey;
        }
        final S3Presigner presigner = presignerProvider.getIfAvailable();
        if (presigner == null) {
            return storedUrlOrKey;
        }
        final String stored = storedUrlOrKey.strip();
        try {
            final ResolvedObject resolved = resolveBucketAndKey(stored);
            if (resolved == null) {
                return storedUrlOrKey;
            }
            final int minutes = Math.max(1, minioProperties.getPresignDurationMinutes());
            final GetObjectRequest getObjectRequest =
                    GetObjectRequest.builder().bucket(resolved.bucket()).key(resolved.key()).build();
            final GetObjectPresignRequest presignRequest =
                    GetObjectPresignRequest.builder()
                            .signatureDuration(Duration.ofMinutes(minutes))
                            .getObjectRequest(getObjectRequest)
                            .build();
            return presigner.presignGetObject(presignRequest).url().toExternalForm();
        } catch (final Exception ex) {
            log.warn("Could not presign media URL, returning original reference: {}", ex.getMessage());
            return storedUrlOrKey;
        }
    }

    private ResolvedObject resolveBucketAndKey(final String stored) {
        final String configuredBucket =
                minioProperties.getBucket() != null ? minioProperties.getBucket().strip() : "";
        final String endpoint = minioProperties.getEndpoint() != null ? minioProperties.getEndpoint().strip() : "";
        if (endpoint.isEmpty()) {
            return null;
        }
        final URI endpointUri = URI.create(endpoint);
        final String endpointHost = endpointUri.getHost();

        if (!stored.startsWith("http://") && !stored.startsWith("https://")) {
            if (configuredBucket.isEmpty()) {
                return null;
            }
            final String key = stripLeadingSlashes(stored);
            if (key.isEmpty()) {
                return null;
            }
            return new ResolvedObject(configuredBucket, key);
        }

        final URI objectUri = URI.create(stored);
        if (objectUri.getHost() == null || !objectUri.getHost().equalsIgnoreCase(endpointHost)) {
            return null;
        }
        final String path = objectUri.getPath();
        if (path == null || path.isEmpty() || "/".equals(path)) {
            return null;
        }
        final String[] segments = path.split("/", -1);
        if (segments.length < 3) {
            return null;
        }
        final String bucketFromPath = segments[1];
        final String key =
                Arrays.stream(segments).skip(2).filter(s -> !s.isEmpty()).collect(Collectors.joining("/"));
        if (bucketFromPath.isEmpty() || key.isEmpty()) {
            return null;
        }
        if (!configuredBucket.isEmpty() && !configuredBucket.equals(bucketFromPath)) {
            log.debug(
                    "Media URL bucket [{}] differs from configured minio.bucket [{}]; using path bucket",
                    bucketFromPath,
                    configuredBucket);
        }
        return new ResolvedObject(bucketFromPath, key);
    }

    private static String stripLeadingSlashes(final String raw) {
        int i = 0;
        while (i < raw.length() && raw.charAt(i) == '/') {
            i++;
        }
        return raw.substring(i);
    }

    private record ResolvedObject(String bucket, String key) {}
}
