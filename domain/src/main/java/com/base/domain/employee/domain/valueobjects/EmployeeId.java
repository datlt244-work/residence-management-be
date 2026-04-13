package com.base.domain.employee.domain.valueobjects;

import jakarta.validation.constraints.NotBlank;

public record EmployeeId(@NotBlank String value) {

    public EmployeeId {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("EmployeeId cannot be null or empty");
        }
    }

    public static EmployeeId of(String value) {
        return new EmployeeId(value);
    }
}
