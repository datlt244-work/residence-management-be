package com.base.infra.config;

import com.base.domain.department.domain.Department;
import com.base.domain.department.repository.DepartmentRepository;
import com.base.domain.employee.domain.Employee;
import com.base.domain.employee.domain.EmployeeRole;
import com.base.domain.employee.repository.EmployeeRepository;
import com.base.domain.user.domain.valueobjects.Email;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataLoader implements CommandLineRunner {

    private static final String DEFAULT_DEPT_CODE = "HQ";

    private final DepartmentRepository departmentRepository;
    private final EmployeeRepository employeeRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) {
        log.info("Checking schema seed data (admin employee)...");
        Email adminEmail = Email.of("admin@admin.com");
        if (employeeRepository.existsByEmail(adminEmail)) {
            log.info("Admin employee already exists, skipping seed.");
            return;
        }

        Department department = departmentRepository.findByCode(DEFAULT_DEPT_CODE)
                .orElseGet(() -> departmentRepository.save(Department.create(DEFAULT_DEPT_CODE, "Head office")));

        String encodedPassword = passwordEncoder.encode("Test123@");
        Integer deptId = Integer.parseInt(department.getId());
        Employee admin = Employee.create(deptId, adminEmail, null, "System Admin", encodedPassword, EmployeeRole.ADMIN);
        employeeRepository.save(admin);
        log.info("Default admin employee created (email: admin@admin.com).");
    }
}
