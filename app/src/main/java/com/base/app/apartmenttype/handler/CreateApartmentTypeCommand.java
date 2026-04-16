package com.base.app.apartmenttype.handler;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateApartmentTypeCommand(
        @NotBlank String zoneId,
        @Size(max = 50) String code,
        @NotBlank @Size(max = 100) String name,
        Integer displayOrder) {}
