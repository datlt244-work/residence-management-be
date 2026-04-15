package com.base.app.project.handler;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateProjectCommand(
        @NotBlank @Size(max = 150) String name, 
        @NotBlank @Size(max = 50) String code
) {}
