package com.base.app.employee.handler;

import com.base.app.employee.command.UpdateEmployeeProfileCommand;
import com.base.app.employee.dto.EmployeeDto;
import com.base.domain.employee.domain.Employee;
import com.base.domain.employee.domain.valueobjects.EmployeeId;
import com.base.domain.employee.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class UpdateEmployeeProfileHandler {

    private final EmployeeRepository employeeRepository;

    public EmployeeDto handle(UpdateEmployeeProfileCommand command) {
        EmployeeId employeeId = EmployeeId.of(command.employeeId());
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new IllegalArgumentException("Employee not found: " + command.employeeId()));

        employee.updateProfile(command.fullName());
        employee = employeeRepository.save(employee);

        return EmployeeDto.fromDomain(employee);
    }
}
