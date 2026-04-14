package com.base.app.department.handler;

import com.base.domain.department.domain.valueobjects.DepartmentId;
import com.base.domain.department.repository.DepartmentRepository;
import com.base.domain.employee.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class DeleteDepartmentAdminHandler {

    private final DepartmentRepository departmentRepository;
    private final EmployeeRepository employeeRepository;

    public void handle(final String departmentId) {
        DepartmentId id = DepartmentId.of(departmentId);
        departmentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Department not found: " + departmentId));

        final int departmentIdInt = Integer.parseInt(departmentId);
        if (employeeRepository.existsByDepartmentId(departmentIdInt)) {
            throw new IllegalArgumentException("Cannot delete department: employees are still assigned to it");
        }

        departmentRepository.deleteById(id);
    }
}
