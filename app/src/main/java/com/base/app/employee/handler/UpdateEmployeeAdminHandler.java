package com.base.app.employee.handler;

import com.base.app.employee.command.UpdateEmployeeAdminCommand;
import com.base.app.employee.dto.EmployeeDto;
import com.base.domain.employee.domain.Employee;
import com.base.domain.employee.domain.EmployeeRole;
import com.base.domain.employee.domain.valueobjects.EmployeeId;
import com.base.domain.employee.repository.EmployeeRepository;
import com.base.domain.user.domain.valueobjects.Email;
import com.base.domain.user.domain.valueobjects.Password;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class UpdateEmployeeAdminHandler {

    private final EmployeeRepository employeeRepository;
    private final PasswordEncoder passwordEncoder;

    public EmployeeDto handle(final String employeeId, final UpdateEmployeeAdminCommand command) {
        Employee employee = employeeRepository.findById(EmployeeId.of(employeeId))
                .orElseThrow(() -> new IllegalArgumentException("Employee not found: " + employeeId));

        Email newEmail = Email.of(command.email().strip());
        employeeRepository.findByEmail(newEmail).ifPresent(other -> {
            if (!other.getId().equals(employee.getId())) {
                throw new IllegalArgumentException("Employee with this email already exists: " + newEmail.value());
            }
        });

        EmployeeRole role = EmployeeRole.STAFF;
        if (command.role() != null && !command.role().isBlank()) {
            role = EmployeeRole.valueOf(command.role().strip().toUpperCase(Locale.ROOT));
        }

        employee.setEmail(newEmail);
        employee.setFullName(command.fullName().strip());
        String phone = command.phone();
        employee.setPhone(phone == null || phone.isBlank() ? null : phone.strip());
        employee.setDepartmentId(command.departmentId());
        employee.setRole(role);

        if (command.password() != null) {
            Password.raw(command.password());
            employee.changePassword(passwordEncoder.encode(command.password()));
        }

        Employee saved = employeeRepository.save(employee);
        log.info("Employee updated id={}", saved.getId());
        return EmployeeDto.fromDomain(saved);
    }
}
