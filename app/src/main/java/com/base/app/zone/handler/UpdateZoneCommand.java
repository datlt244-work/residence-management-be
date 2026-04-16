package com.base.app.zone.handler;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateZoneCommand(@NotBlank @Size(max = 150) String name) {}
