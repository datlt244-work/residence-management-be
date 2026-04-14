package com.base.app.admin.dto;

import com.base.domain.employee.domain.Employee;
import com.base.domain.employee.domain.EmployeeRole;

public record AdminEmployeeDto(
        String id,
        String fullName,
        String email,
        String phoneNumber,
        String role,
        String roleDisplayName,
        boolean active
) {

    public static AdminEmployeeDto of(final Employee employee) {
        if (employee == null) {
            return new AdminEmployeeDto("", "", "", "", "", "", false);
        }
        EmployeeRole userRole = employee.getRole();
        String emailVal = employee.getEmail() != null && employee.getEmail().value() != null
                ? employee.getEmail().value()
                : "";
        String phone = employee.getPhone() != null ? employee.getPhone() : "";
        String name = employee.getFullName() != null ? employee.getFullName() : "";
        return new AdminEmployeeDto(
                employee.getId() != null ? employee.getId() : "",
                name,
                emailVal,
                phone,
                userRole != null ? userRole.name() : "",
                userRole != null ? userRole.getDisplayName() : "",
                Boolean.TRUE.equals(employee.getIsActive()));
    }
}
