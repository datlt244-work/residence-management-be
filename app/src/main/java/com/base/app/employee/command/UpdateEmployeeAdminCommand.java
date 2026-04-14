package com.base.app.employee.command;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UpdateEmployeeAdminCommand(
        @NotBlank @Email String email,
        @NotBlank @Size(max = 100) String fullName,
        @Pattern(regexp = "^(?i)(ADMIN|MANAGER|STAFF)?$", message = "Invalid role")
        String role,
        Integer departmentId,
        @Size(max = 20) String phone,
        @Size(min = 8, max = 100) String password,
        @Size(min = 8, max = 100) String confirmPassword
) {
}
