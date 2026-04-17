package com.base.app.security;

import com.base.domain.employee.domain.EmployeeRole;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

public final class CurrentEmployeeRoleSupport {

    private CurrentEmployeeRoleSupport() {}

    public static EmployeeRole requireEmployeeRole() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("Not authenticated");
        }
        for (GrantedAuthority authority : authentication.getAuthorities()) {
            if ("ROLE_ADMIN".equals(authority.getAuthority())) {
                return EmployeeRole.ADMIN;
            }
        }
        for (GrantedAuthority authority : authentication.getAuthorities()) {
            if ("ROLE_MANAGER".equals(authority.getAuthority())) {
                return EmployeeRole.MANAGER;
            }
        }
        for (GrantedAuthority authority : authentication.getAuthorities()) {
            if ("ROLE_STAFF".equals(authority.getAuthority())) {
                return EmployeeRole.STAFF;
            }
        }
        throw new IllegalStateException("No employee role in security context");
    }
}
