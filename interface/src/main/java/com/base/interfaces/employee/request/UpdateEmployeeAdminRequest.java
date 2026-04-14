package com.base.interfaces.employee.request;

import com.base.app.employee.command.UpdateEmployeeAdminCommand;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateEmployeeAdminRequest(
        @NotBlank(message = "Email is required")
        @Email(message = "Invalid email format")
        String email,

        @NotBlank(message = "Full name is required")
        @Size(max = 100, message = "Full name must not exceed 100 characters")
        String fullName,

        String role,
        Integer departmentId,
        @Size(max = 20, message = "Phone must not exceed 20 characters")
        String phone,

        @Size(min = 8, message = "Password must be at least 8 characters")
        String password,

        @Size(min = 8, message = "Confirm password must be at least 8 characters")
        String confirmPassword
) {

    public UpdateEmployeeAdminRequest {
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
        if (password != null) {
            password = password.trim();
            if (password.isEmpty()) {
                password = null;
            }
        }
        if (confirmPassword != null) {
            confirmPassword = confirmPassword.trim();
            if (confirmPassword.isEmpty()) {
                confirmPassword = null;
            }
        }
    }

    public UpdateEmployeeAdminCommand toCommand() {
        return new UpdateEmployeeAdminCommand(email, fullName, role, departmentId, phone, password, confirmPassword);
    }
}
