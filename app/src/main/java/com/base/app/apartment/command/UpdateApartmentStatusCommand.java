package com.base.app.apartment.command;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateApartmentStatusCommand(
        @NotBlank(message = "Status is required") @Size(max = 50, message = "Status must not exceed 50 characters")
                String status) {

    public UpdateApartmentStatusCommand {
        if (status != null) {
            status = status.strip();
        }
    }
}
