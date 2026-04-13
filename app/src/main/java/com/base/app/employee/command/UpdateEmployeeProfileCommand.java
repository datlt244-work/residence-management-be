package com.base.app.employee.command;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateEmployeeProfileCommand(
        @NotBlank String employeeId,
        @NotBlank @Size(max = 100) String fullName
) {

    public UpdateEmployeeProfileCommand {
        if (fullName != null) {
            fullName = fullName.trim();
        }
    }

    public static UpdateEmployeeProfileCommand of(String employeeId, String fullName) {
        return new UpdateEmployeeProfileCommand(employeeId, fullName);
    }
}
