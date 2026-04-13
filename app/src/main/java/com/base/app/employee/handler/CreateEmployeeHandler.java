package com.base.app.employee.handler;

import com.base.app.employee.command.CreateEmployeeCommand;
import com.base.app.employee.dto.EmployeeDto;
import com.base.domain.employee.domain.Employee;
import com.base.domain.employee.domain.EmployeeRole;
import com.base.domain.employee.repository.EmployeeRepository;
import com.base.domain.user.domain.valueobjects.Email;
import com.base.domain.user.domain.valueobjects.Password;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class CreateEmployeeHandler {

    private final EmployeeRepository employeeRepository;
    private final PasswordEncoder passwordEncoder;

    public EmployeeDto handle(CreateEmployeeCommand command) {
        log.info("Creating employee for email: {}", command.email());

        validateInput(command);

        Email email = Email.of(command.email());
        EmployeeRole role = EmployeeRole.STAFF;
        if (command.role() != null && !command.role().isBlank()) {
            try {
                role = EmployeeRole.valueOf(command.role());
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Invalid role. Valid values: ADMIN, MANAGER, STAFF");
            }
        }

        String encodedPassword = passwordEncoder.encode(command.password());
        Employee employee = Employee.create(
                command.departmentId(),
                email,
                command.phone(),
                command.fullName(),
                encodedPassword,
                role);
        employee = employeeRepository.save(employee);

        log.info("Employee created id={}", employee.getId());
        return EmployeeDto.fromDomain(employee);
    }

    private void validateInput(CreateEmployeeCommand command) {
        if (command.email() == null || command.email().isBlank()) {
            throw new IllegalArgumentException("Email must be provided");
        }
        if (command.fullName() == null || command.fullName().isBlank()) {
            throw new IllegalArgumentException("Full name is required");
        }
        Email email = Email.of(command.email());
        if (employeeRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Employee with this email already exists: " + email.value());
        }
        Password.raw(command.password());
    }
}
