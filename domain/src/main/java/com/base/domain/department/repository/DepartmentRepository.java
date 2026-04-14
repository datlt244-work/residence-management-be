package com.base.domain.department.repository;

import com.base.domain.department.domain.Department;
import com.base.domain.department.domain.valueobjects.DepartmentId;
import com.base.domain.shared.PageResult;

import java.time.LocalDate;
import java.util.Optional;

public interface DepartmentRepository {

    Department save(Department department);

    Optional<Department> findById(DepartmentId id);

    Optional<Department> findByCode(String code);

    boolean existsByCode(String code);

    PageResult<Department> searchDepartments(
            String codeSearch,
            String nameSearch,
            LocalDate createdFromInclusive,
            LocalDate createdToInclusive,
            int page,
            int size);
}
