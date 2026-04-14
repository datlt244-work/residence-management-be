package com.base.domain.employee.repository;

import com.base.domain.employee.domain.Employee;
import com.base.domain.employee.domain.EmployeeRole;
import com.base.domain.employee.domain.valueobjects.EmployeeId;
import com.base.domain.shared.PageResult;
import com.base.domain.user.domain.valueobjects.Email;

import java.util.Optional;

public interface EmployeeRepository {

    Employee save(Employee employee);

    Optional<Employee> findById(EmployeeId id);

    Optional<Employee> findByEmail(Email email);

    boolean existsByEmail(Email email);

    /**
     * Admin list: paged employees with optional role, active flag, and text search (email, full name, phone).
     */
    PageResult<Employee> searchForAdmin(
            EmployeeRole roleFilter,
            Boolean active,
            String search,
            int page,
            int size);

    void deleteById(EmployeeId id);
}
