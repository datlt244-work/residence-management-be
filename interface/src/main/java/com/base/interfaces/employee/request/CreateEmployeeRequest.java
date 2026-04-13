package com.base.interfaces.employee.request;

import com.base.app.employee.command.CreateEmployeeCommand;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateEmployeeRequest(
        @NotBlank(message = "Email is required")
        @Email(message = "Invalid email format")
        String email,

        @NotBlank(message = "Full name is required")
        @Size(max = 100, message = "Full name must not exceed 100 characters")
        String fullName,

        @NotBlank(message = "Password is required")
        @Size(min = 8, message = "Password must be at least 8 characters")
        String password,

        String role,
        Integer departmentId,
        @Size(max = 20, message = "Phone must not exceed 20 characters")
        String phone
) {

    public CreateEmployeeRequest {
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

    public CreateEmployeeCommand toCommand() {
        return new CreateEmployeeCommand(email, fullName, password, role, departmentId, phone);
    }
}
