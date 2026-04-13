package com.base.interfaces.employee.request;

import com.base.app.auth.command.ChangePasswordCommand;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ChangePasswordRequest(
        @NotBlank(message = "Current password is required")
        String currentPassword,

        @NotBlank(message = "New password is required")
        @Size(min = 8, message = "New password must be at least 8 characters")
        String newPassword
) {

    public ChangePasswordCommand toCommand(String email) {
        return ChangePasswordCommand.of(email, currentPassword, newPassword);
    }
}
