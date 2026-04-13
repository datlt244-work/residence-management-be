package com.base.domain.employee.domain;

import com.base.domain.shared.BaseDomain;
import com.base.domain.user.domain.valueobjects.Email;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;

@Getter
@Setter
@NoArgsConstructor
public class Employee extends BaseDomain {

    private Integer departmentId;
    private String fullName;
    private String phone;
    private Email email;
    private String passwordHash;
    private EmployeeRole role = EmployeeRole.STAFF;
    private Boolean isActive = true;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static Employee create(
            Integer departmentId,
            Email email,
            String phone,
            String fullName,
            String encodedPasswordHash,
            EmployeeRole role) {
        Employee employee = new Employee();
        employee.setDepartmentId(departmentId);
        employee.setEmail(email);
        employee.setPhone(phone);
        employee.setFullName(fullName);
        employee.setPasswordHash(encodedPasswordHash);
        employee.setRole(role != null ? role : EmployeeRole.STAFF);
        employee.setIsActive(true);
        return employee;
    }

    public Collection<String> getAuthorities() {
        return Collections.singletonList("ROLE_" + role.name());
    }

    public boolean canLogin() {
        return Boolean.TRUE.equals(isActive);
    }

    public void updateProfile(String newFullName) {
        this.fullName = newFullName;
    }

    public void changePassword(String newEncodedPasswordHash) {
        this.passwordHash = newEncodedPasswordHash;
    }

    public boolean hasRole(EmployeeRole expected) {
        return this.role == expected;
    }
}
