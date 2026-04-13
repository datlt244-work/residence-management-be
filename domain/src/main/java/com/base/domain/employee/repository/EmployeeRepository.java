package com.base.domain.employee.repository;

import com.base.domain.employee.domain.Employee;
import com.base.domain.employee.domain.valueobjects.EmployeeId;
import com.base.domain.user.domain.valueobjects.Email;

import java.util.Optional;

public interface EmployeeRepository {

    Employee save(Employee employee);

    Optional<Employee> findById(EmployeeId id);

    Optional<Employee> findByEmail(Email email);

    boolean existsByEmail(Email email);
}
