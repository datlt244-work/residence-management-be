package com.base.app.employee.command;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateEmployeeCommand(
        @NotBlank(message = "Email is required")
        @Email String email,
        @NotBlank @Size(max = 100) String fullName,
        @NotBlank @Size(min = 8) String password,
        String role,
        Integer departmentId,
        @Size(max = 20) String phone
) {

    public CreateEmployeeCommand {
        if (email != null) {
            email = email.toLowerCase().trim();
        }
        if (fullName != null) {
            fullName = fullName.trim();
        }
        if (role != null) {
            role = role.toUpperCase().trim();
        }
        if (phone != null) {
            phone = phone.trim();
        }
    }

    public static CreateEmployeeCommand of(String email, String fullName, String password, String role) {
        return new CreateEmployeeCommand(email, fullName, password, role, null, null);
    }
}
