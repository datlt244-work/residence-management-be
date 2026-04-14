package com.base.app.department.command;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateDepartmentCommand(
        @NotBlank @Size(max = 50) String code,
        @NotBlank @Size(max = 100) String name
) {
}
