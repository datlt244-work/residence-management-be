package com.base.infra.department.repository.impl;

import com.base.domain.department.domain.Department;
import com.base.domain.department.domain.valueobjects.DepartmentId;
import com.base.domain.department.repository.DepartmentRepository;
import com.base.domain.shared.PageResult;
import com.base.infra.department.entity.DepartmentEntity;
import com.base.infra.department.mapper.DepartmentMapper;
import com.base.infra.department.repository.JpaDepartmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
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

    @Override
    public PageResult<Department> searchDepartments(
            final String codeSearch,
            final String nameSearch,
            final LocalDate createdFromInclusive,
            final LocalDate createdToInclusive,
            final int page,
            final int size) {

        final String codePattern = toLikePattern(codeSearch);
        final String namePattern = toLikePattern(nameSearch);
        final LocalDateTime createdFrom =
                createdFromInclusive == null ? null : createdFromInclusive.atStartOfDay();
        final LocalDateTime createdToExclusive =
                createdToInclusive == null ? null : createdToInclusive.plusDays(1).atStartOfDay();

        Page<DepartmentEntity> pageResult = jpaRepository.searchDepartments(
                codePattern,
                namePattern,
                createdFrom,
                createdToExclusive,
                PageRequest.of(page, size));

        List<Department> rows = new ArrayList<>(pageResult.getNumberOfElements());
        for (DepartmentEntity entity : pageResult.getContent()) {
            rows.add(mapper.toDomain(entity));
        }

        return new PageResult<>(
                rows,
                pageResult.getNumber(),
                pageResult.getSize(),
                pageResult.getTotalElements(),
                pageResult.getTotalPages());
    }

    private static String toLikePattern(final String raw) {
        if (raw == null || raw.isBlank()) {
            return null;
        }
        final String escaped = escapeLikePattern(raw.trim().toLowerCase(Locale.ROOT));
        return "%" + escaped + "%";
    }

    private static String escapeLikePattern(final String raw) {
        return raw.replace("\\", "\\\\").replace("%", "\\%").replace("_", "\\_");
    }
}
