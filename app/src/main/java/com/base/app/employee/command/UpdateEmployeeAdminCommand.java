package com.base.app.employee.command;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateEmployeeAdminCommand(
        @NotBlank @Email String email,
        @NotBlank @Size(max = 100) String fullName,
        String role,
        Integer departmentId,
        @Size(max = 20) String phone,
        String password,
        String confirmPassword
) {

    public UpdateEmployeeAdminCommand {
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
}
