package com.base.app.department.handler;

import com.base.app.department.command.CreateDepartmentCommand;
import com.base.app.department.dto.DepartmentListItemDto;
import com.base.domain.department.domain.Department;
import com.base.domain.department.repository.DepartmentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class CreateDepartmentHandler {

    private final DepartmentRepository departmentRepository;

    public DepartmentListItemDto handle(final CreateDepartmentCommand command) {
        final String code = command.code().strip();
        final String name = command.name().strip();
        if (departmentRepository.existsByCode(code)) {
            throw new IllegalArgumentException("Department with this code already exists: " + code);
        }
        Department department = Department.create(code, name);
        Department saved = departmentRepository.save(department);
        log.info("Department created id={}", saved.getId());
        return DepartmentListItemDto.fromDomain(saved);
    }
}
