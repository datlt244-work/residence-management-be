package com.base.app.department.dto;

import com.base.domain.department.domain.Department;

import java.time.LocalDateTime;

public record DepartmentListItemDto(
        String id,
        String code,
        String name,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {

    public static DepartmentListItemDto fromDomain(final Department department) {
        return new DepartmentListItemDto(
                department.getId(),
                department.getCode(),
                department.getName(),
                department.getCreatedAt(),
                department.getUpdatedAt());
    }
}
