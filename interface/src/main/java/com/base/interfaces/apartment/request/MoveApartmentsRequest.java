package com.base.interfaces.apartment.request;

import com.base.app.apartment.command.MoveApartmentsCommand;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.ArrayList;
import java.util.List;

public record MoveApartmentsRequest(
        @NotEmpty(message = "At least one apartment id is required")
        @Size(max = 500, message = "At most 500 apartments per request")
        List<@NotBlank(message = "Apartment id must not be blank")
                @Size(max = 32, message = "Apartment id is too long")
                String> apartmentIds,
        @NotBlank(message = "Target zone id is required") @Size(max = 32) String targetZoneId,
        @NotBlank(message = "Target apartment type id is required") @Size(max = 32) String targetApartmentTypeId) {

    public MoveApartmentsRequest {
        if (targetZoneId != null) {
            targetZoneId = targetZoneId.strip();
        }
        if (targetApartmentTypeId != null) {
            targetApartmentTypeId = targetApartmentTypeId.strip();
        }
        if (apartmentIds != null) {
            List<String> normalized = new ArrayList<>(apartmentIds.size());
            for (String id : apartmentIds) {
                normalized.add(id != null ? id.strip() : null);
            }
            apartmentIds = List.copyOf(normalized);
        }
    }

    public MoveApartmentsCommand toCommand() {
        return new MoveApartmentsCommand(apartmentIds, targetZoneId, targetApartmentTypeId);
    }
}
