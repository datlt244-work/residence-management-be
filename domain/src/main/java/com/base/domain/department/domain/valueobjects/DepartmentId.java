package com.base.domain.department.domain.valueobjects;

import jakarta.validation.constraints.NotBlank;

public record DepartmentId(@NotBlank String value) {

    public DepartmentId {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("DepartmentId cannot be null or empty");
        }
    }

    public static DepartmentId of(String value) {
        return new DepartmentId(value);
    }
}
