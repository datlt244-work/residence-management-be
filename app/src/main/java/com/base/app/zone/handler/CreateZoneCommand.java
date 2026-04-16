package com.base.app.zone.handler;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateZoneCommand(
        @NotBlank String projectId,
        @NotBlank @Size(max = 50) String code,
        @NotBlank @Size(max = 150) String name,
        Integer displayOrder) {}
