package com.base.infra.employee.repository.impl;

import com.base.domain.employee.domain.Employee;
import com.base.domain.employee.domain.EmployeeRole;
import com.base.domain.employee.domain.valueobjects.EmployeeId;
import com.base.domain.employee.repository.EmployeeRepository;
import com.base.domain.shared.PageResult;
import com.base.domain.user.domain.valueobjects.Email;
import com.base.infra.employee.entity.EmployeeEntity;
import com.base.infra.employee.mapper.EmployeeMapper;
import com.base.infra.employee.repository.JpaEmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class EmployeeRepositoryImpl implements EmployeeRepository {

    private final JpaEmployeeRepository jpaRepository;
    private final EmployeeMapper mapper;

    @Override
    public Employee save(Employee employee) {
        EmployeeEntity entity = mapper.toEntity(employee);
        EmployeeEntity saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<Employee> findById(EmployeeId id) {
        return jpaRepository.findById(Integer.parseInt(id.value()))
                .map(mapper::toDomain);
    }

    @Override
    public Optional<Employee> findByEmail(Email email) {
        if (email == null || email.value() == null) {
            return Optional.empty();
        }
        return jpaRepository.findByEmail(email.value())
                .map(mapper::toDomain);
    }

    @Override
    public boolean existsByEmail(Email email) {
        if (email == null || email.value() == null) {
            return false;
        }
        return jpaRepository.existsByEmail(email.value());
    }

    @Override
    public PageResult<Employee> searchForAdmin(
            final EmployeeRole roleFilter,
            final Boolean active,
            final String search,
            final int page,
            final int size) {

        final String searchPattern;
        if (search != null && !search.isBlank()) {
            final String escapedForLike = escapeLikePattern(search.trim().toLowerCase(Locale.ROOT));
            searchPattern = "%" + escapedForLike + "%";
        } else {
            searchPattern = null;
        }

        Page<EmployeeEntity> employeeEntityPage = jpaRepository.findEmployeesForAdmin(
                roleFilter,
                active,
                searchPattern,
                PageRequest.of(page, size));

        List<Employee> employees = new ArrayList<>(employeeEntityPage.getNumberOfElements());
        for (EmployeeEntity employeeEntity : employeeEntityPage.getContent()) {
            Employee employee = mapper.toDomain(employeeEntity);
            employee.setPasswordHash(null);
            employees.add(employee);
        }

        return new PageResult<>(
                employees,
                employeeEntityPage.getNumber(),
                employeeEntityPage.getSize(),
                employeeEntityPage.getTotalElements(),
                employeeEntityPage.getTotalPages());
    }

    @Override
    public void deleteById(final EmployeeId id) {
        jpaRepository.deleteById(Integer.parseInt(id.value()));
    }

    private static String escapeLikePattern(final String raw) {
        return raw.replace("\\", "\\\\").replace("%", "\\%").replace("_", "\\_");
    }
}
