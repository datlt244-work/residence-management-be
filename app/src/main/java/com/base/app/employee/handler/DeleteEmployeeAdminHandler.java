package com.base.app.employee.handler;

import com.base.domain.employee.domain.valueobjects.EmployeeId;
import com.base.domain.employee.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class DeleteEmployeeAdminHandler {

    private final EmployeeRepository employeeRepository;

    public void handle(final String employeeId, final String currentEmployeeIdOrNull) {
        EmployeeId id = EmployeeId.of(employeeId);
        employeeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Employee not found: " + employeeId));

        if (currentEmployeeIdOrNull != null && currentEmployeeIdOrNull.equals(employeeId)) {
            throw new IllegalArgumentException("You cannot delete your own account");
        }

        employeeRepository.deleteById(id);
    }
}
