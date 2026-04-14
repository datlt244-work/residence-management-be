package com.base.app.admin.handler;

import com.base.app.admin.dto.AdminEmployeeDto;
import com.base.domain.employee.domain.EmployeeRole;
import com.base.domain.employee.repository.EmployeeRepository;
import com.base.domain.shared.PageResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

@Service
@RequiredArgsConstructor
public class AdminEmployeeHandler {

    private static final int DEFAULT_SIZE = 20;
    private static final int MAX_SIZE = 100;

    private final EmployeeRepository employeeRepository;

    @Transactional(readOnly = true)
    public PageResult<AdminEmployeeDto> handle(
            final int page,
            final int size,
            final String search,
            final String roleParam,
            final Boolean active) {
        final int normalizedSize = size <= 0 ? DEFAULT_SIZE : Math.min(size, MAX_SIZE);
        final int normalizedPage = Math.max(0, page);
        final EmployeeRole roleFilter = parseRole(roleParam);

        return employeeRepository
                .searchForAdmin(roleFilter, active, search, normalizedPage, normalizedSize)
                .map(AdminEmployeeDto::of);
    }

    private static EmployeeRole parseRole(final String roleParam) {
        if (roleParam == null || roleParam.isBlank()) {
            return null;
        }
        try {
            return EmployeeRole.valueOf(roleParam.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid role. Allowed values: ADMIN, MANAGER, STAFF.");
        }
    }
}
