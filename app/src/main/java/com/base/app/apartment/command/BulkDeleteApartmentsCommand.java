package com.base.app.apartment.command;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.ArrayList;
import java.util.List;

public record BulkDeleteApartmentsCommand(
        @NotEmpty(message = "At least one apartment id is required")
        @Size(max = 500, message = "At most 500 apartments per request")
        List<@NotBlank(message = "Apartment id must not be blank")
                @Size(max = 32, message = "Apartment id is too long")
                String> apartmentIds) {

    public BulkDeleteApartmentsCommand {
        if (apartmentIds != null) {
            List<String> normalized = new ArrayList<>(apartmentIds.size());
            for (String id : apartmentIds) {
                normalized.add(id != null ? id.strip() : null);
            }
            apartmentIds = List.copyOf(normalized);
        }
    }
}
