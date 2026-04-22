package com.base.infra.config.storage;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "minio")
@Getter
@Setter
public class MinioProperties {

    private boolean enabled = true;
    private String endpoint = "";
    private String accessKey = "";
    private String secretKey = "";
    private String region = "us-east-1";
    private String bucket = "";
    private boolean pathStyleAccess = true;
    private int presignDurationMinutes = 15;

    private long maxUploadBytes = 104_857_600L;
}
