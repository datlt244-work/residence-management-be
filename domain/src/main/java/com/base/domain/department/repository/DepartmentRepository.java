package com.base.domain.department.repository;

import com.base.domain.department.domain.Department;
import com.base.domain.department.domain.valueobjects.DepartmentId;

import java.util.Optional;

public interface DepartmentRepository {

    Department save(Department department);

    Optional<Department> findById(DepartmentId id);

    Optional<Department> findByCode(String code);

    boolean existsByCode(String code);
}
