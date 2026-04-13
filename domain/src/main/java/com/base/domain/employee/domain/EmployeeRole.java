package com.base.domain.employee.domain;

/**
 * Maps to {@code employees.role} in schema (ADMIN, MANAGER, STAFF).
 */
public enum EmployeeRole {
    ADMIN("Administrator"),
    MANAGER("Manager"),
    STAFF("Staff");

    private final String displayName;

    EmployeeRole(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
