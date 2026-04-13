package com.base.app.employee.dto;

import com.base.domain.employee.domain.Employee;

public record EmployeeDto(
        String id,
        String email,
        String fullName,
        String phone,
        Integer departmentId,
        String role,
        Boolean isActive
) {

    public static EmployeeDto fromDomain(Employee employee) {
        String fullName = employee.getFullName() != null ? employee.getFullName() : "";
        return new EmployeeDto(
                employee.getId(),
                employee.getEmail() != null ? employee.getEmail().value() : null,
                fullName,
                employee.getPhone(),
                employee.getDepartmentId(),
                employee.getRole() != null ? employee.getRole().name() : null,
                employee.getIsActive()
        );
    }

    public Boolean isActive() {
        return Boolean.TRUE.equals(isActive);
    }

    public EmployeeDto withActive(boolean active) {
        return new EmployeeDto(id, email, fullName, phone, departmentId, role, active);
    }
}
