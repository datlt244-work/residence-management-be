package com.base.app.department.handler;

import com.base.app.department.command.UpdateDepartmentAdminCommand;
import com.base.app.department.dto.DepartmentListItemDto;
import com.base.domain.department.domain.Department;
import com.base.domain.department.domain.valueobjects.DepartmentId;
import com.base.domain.department.repository.DepartmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class UpdateDepartmentAdminHandler {

    private final DepartmentRepository departmentRepository;

    public DepartmentListItemDto handle(final String departmentId, final UpdateDepartmentAdminCommand command) {
        Department department = departmentRepository.findById(DepartmentId.of(departmentId))
                .orElseThrow(() -> new IllegalArgumentException("Department not found: " + departmentId));

        final String newCode = command.code().strip();
        final String newName = command.name().strip();

        departmentRepository.findByCode(newCode).ifPresent(other -> {
            if (!other.getId().equals(department.getId())) {
                throw new IllegalArgumentException("Department with this code already exists: " + newCode);
            }
        });

        department.setCode(newCode);
        department.setName(newName);
        Department saved = departmentRepository.save(department);
        return DepartmentListItemDto.fromDomain(saved);
    }
}
