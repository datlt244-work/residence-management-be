package com.base.app.project.handler;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateProjectCommand(@NotBlank @Size(max = 150) String name) {}
