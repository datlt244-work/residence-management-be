package com.base.infra.department.repository.impl;

import com.base.domain.department.domain.Department;
import com.base.domain.department.domain.valueobjects.DepartmentId;
import com.base.domain.department.repository.DepartmentRepository;
import com.base.infra.department.entity.DepartmentEntity;
import com.base.infra.department.mapper.DepartmentMapper;
import com.base.infra.department.repository.JpaDepartmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class DepartmentRepositoryImpl implements DepartmentRepository {

    private final JpaDepartmentRepository jpaRepository;
    private final DepartmentMapper mapper;

    @Override
    public Department save(Department department) {
        DepartmentEntity entity = mapper.toEntity(department);
        DepartmentEntity saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<Department> findById(DepartmentId id) {
        return jpaRepository.findById(Integer.parseInt(id.value()))
                .map(mapper::toDomain);
    }

    @Override
    public Optional<Department> findByCode(String code) {
        return jpaRepository.findByCode(code)
                .map(mapper::toDomain);
    }

    @Override
    public boolean existsByCode(String code) {
        return jpaRepository.existsByCode(code);
    }
}
