package com.base.interfaces.employee.request;

import com.base.app.employee.command.UpdateEmployeeProfileCommand;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateEmployeeProfileRequest(
        @NotBlank(message = "Full name is required")
        @Size(max = 100, message = "Full name must not exceed 100 characters")
        String fullName
) {

    public UpdateEmployeeProfileRequest {
        if (fullName != null) {
            fullName = fullName.trim();
        }
    }

    public UpdateEmployeeProfileCommand toCommand(String employeeId) {
        return UpdateEmployeeProfileCommand.of(employeeId, fullName);
    }
}
