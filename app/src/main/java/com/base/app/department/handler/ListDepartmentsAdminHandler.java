package com.base.app.department.handler;

import com.base.app.department.dto.DepartmentListItemDto;
import com.base.domain.department.repository.DepartmentRepository;
import com.base.domain.shared.PageResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class ListDepartmentsAdminHandler {

    private static final int DEFAULT_SIZE = 20;
    private static final int MAX_SIZE = 100;

    private final DepartmentRepository departmentRepository;

    @Transactional(readOnly = true)
    public PageResult<DepartmentListItemDto> handle(
            final int page,
            final int size,
            final String code,
            final String name,
            final LocalDate createdFrom,
            final LocalDate createdTo) {

        if (createdFrom != null && createdTo != null && createdFrom.isAfter(createdTo)) {
            throw new IllegalArgumentException("createdFrom must be on or before createdTo");
        }

        final int normalizedSize = size <= 0 ? DEFAULT_SIZE : Math.min(size, MAX_SIZE);
        final int normalizedPage = Math.max(0, page);

        return departmentRepository
                .searchDepartments(code, name, createdFrom, createdTo, normalizedPage, normalizedSize)
                .map(DepartmentListItemDto::fromDomain);
    }
}
