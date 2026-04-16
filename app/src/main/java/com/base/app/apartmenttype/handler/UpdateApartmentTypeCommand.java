package com.base.app.apartmenttype.handler;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateApartmentTypeCommand(@NotBlank @Size(max = 100) String name) {}
