package com.base.app.employee.dto;

import com.base.domain.department.domain.Department;
import com.base.domain.employee.domain.Employee;

public record EmployeeAdminDetailDto(
        String id,
        String email,
        String fullName,
        String phone,
        Integer departmentId,
        String departmentName,
        String role,
        Boolean isActive
) {

    public static EmployeeAdminDetailDto from(final Employee employee, final Department departmentOrNull) {
        String fullName = employee.getFullName() != null ? employee.getFullName() : "";
        String departmentName = null;
        if (departmentOrNull != null && departmentOrNull.getName() != null) {
            departmentName = departmentOrNull.getName();
        }
        return new EmployeeAdminDetailDto(
                employee.getId(),
                employee.getEmail() != null ? employee.getEmail().value() : null,
                fullName,
                employee.getPhone(),
                employee.getDepartmentId(),
                departmentName,
                employee.getRole() != null ? employee.getRole().name() : null,
                employee.getIsActive());
    }
}
