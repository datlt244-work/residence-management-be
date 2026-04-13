package com.base.app.auth.command;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ChangePasswordCommand(
        @NotBlank String email,
        @NotBlank String currentPassword,
        @NotBlank @Size(min = 8) String newPassword
) {

    public static ChangePasswordCommand of(String email, String currentPassword, String newPassword) {
        return new ChangePasswordCommand(email, currentPassword, newPassword);
    }
}
