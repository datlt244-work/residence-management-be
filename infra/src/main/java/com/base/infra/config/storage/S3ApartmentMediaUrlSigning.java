package com.base.infra.config.storage;

import com.base.domain.apartment.ApartmentMediaObjectStorage;
import com.base.domain.apartment.ApartmentMediaUrlSigning;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

import java.net.URI;
import java.time.Duration;
import java.util.Arrays;
import java.util.Locale;
import java.util.UUID;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class S3ApartmentMediaUrlSigning implements ApartmentMediaUrlSigning, ApartmentMediaObjectStorage {

    private static final Pattern SAFE_NAME = Pattern.compile("[^a-zA-Z0-9._-]+");

    private final MinioProperties minioProperties;
    private final ObjectProvider<S3Presigner> presignerProvider;
    private final ObjectProvider<S3Client> s3ClientProvider;

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

    @Override
    public String putObject(
            final String apartmentId, final String originalFilename, final String contentType, final byte[] content) {
        final S3Client s3Client = s3ClientProvider.getIfAvailable();
        if (s3Client == null) {
            throw new IllegalStateException(
                    "Object storage is disabled (minio.enabled=false); apartment media upload is not available.");
        }
        final String bucket =
                minioProperties.getBucket() != null ? minioProperties.getBucket().strip() : "";
        if (bucket.isEmpty()) {
            throw new IllegalStateException(
                    "minio.bucket is empty. Set MINIO_BUCKET or minio.bucket in configuration, "
                            + "and ensure that bucket exists on the MinIO/S3 server.");
        }
        final long max = minioProperties.getMaxUploadBytes();
        if (content.length > max) {
            throw new IllegalArgumentException("File exceeds maximum allowed size of " + max + " bytes");
        }
        if (content.length == 0) {
            throw new IllegalArgumentException("Empty file");
        }

        final String safe = sanitizeFilename(originalFilename);
        final String key = "apartments/" + apartmentId.strip() + "/" + UUID.randomUUID() + "_" + safe;
        final String ct =
                contentType != null && !contentType.isBlank() ? contentType.strip() : "application/octet-stream";

        s3Client.putObject(
                PutObjectRequest.builder()
                        .bucket(bucket)
                        .key(key)
                        .contentType(ct)
                        .contentLength((long) content.length)
                        .build(),
                RequestBody.fromBytes(content));
        return key;
    }

    @Override
    public void deleteObject(final String storageKeyOrUrl) {
        if (storageKeyOrUrl == null || storageKeyOrUrl.isBlank()) {
            return;
        }
        final S3Client s3Client = s3ClientProvider.getIfAvailable();
        if (s3Client == null) {
            return;
        }
        final String stored = storageKeyOrUrl.strip();
        final ResolvedObject resolved = resolveBucketAndKey(stored);
        if (resolved == null) {
            throw new IllegalStateException("Could not resolve bucket/key for delete: " + stored);
        }
        s3Client.deleteObject(DeleteObjectRequest.builder().bucket(resolved.bucket()).key(resolved.key()).build());
    }

    private static String sanitizeFilename(final String originalFilename) {
        if (originalFilename == null || originalFilename.isBlank()) {
            return "upload.bin";
        }
        String base = originalFilename.strip();
        final int slash = Math.max(base.lastIndexOf('/'), base.lastIndexOf('\\'));
        if (slash >= 0 && slash < base.length() - 1) {
            base = base.substring(slash + 1);
        }
        String cleaned = SAFE_NAME.matcher(base).replaceAll("_");
        if (cleaned.isBlank()) {
            cleaned = "upload.bin";
        }
        if (cleaned.length() > 120) {
            cleaned = cleaned.substring(0, 120);
        }
        return cleaned.toLowerCase(Locale.ROOT);
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
