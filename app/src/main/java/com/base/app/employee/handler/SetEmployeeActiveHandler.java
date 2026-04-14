package com.base.app.employee.handler;

import com.base.app.employee.command.SetEmployeeActiveCommand;
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
public class SetEmployeeActiveHandler {

    private final EmployeeRepository employeeRepository;

    public EmployeeDto handle(final String employeeId, final SetEmployeeActiveCommand command) {
        Employee employee = employeeRepository.findById(EmployeeId.of(employeeId))
                .orElseThrow(() -> new IllegalArgumentException("Employee not found: " + employeeId));
        employee.setIsActive(command.active());
        Employee saved = employeeRepository.save(employee);
        return EmployeeDto.fromDomain(saved);
    }
}
