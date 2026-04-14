package com.base.app.employee.handler;

import com.base.app.employee.dto.EmployeeAdminDetailDto;
import com.base.domain.department.domain.Department;
import com.base.domain.department.domain.valueobjects.DepartmentId;
import com.base.domain.department.repository.DepartmentRepository;
import com.base.domain.employee.domain.Employee;
import com.base.domain.employee.domain.valueobjects.EmployeeId;
import com.base.domain.employee.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GetEmployeeAdminDetailHandler {

    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;

    public EmployeeAdminDetailDto handle(final String employeeId) {
        Employee employee = employeeRepository.findById(EmployeeId.of(employeeId))
                .orElseThrow(() -> new IllegalArgumentException("Employee not found: " + employeeId));

        Department department = resolveDepartment(employee);
        return EmployeeAdminDetailDto.from(employee, department);
    }

    private Department resolveDepartment(final Employee employee) {
        if (employee.getDepartmentId() == null) {
            return null;
        }
        return departmentRepository.findById(DepartmentId.of(String.valueOf(employee.getDepartmentId())))
                .orElse(null);
    }
}
