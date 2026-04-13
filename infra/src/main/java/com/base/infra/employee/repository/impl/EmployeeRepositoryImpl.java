package com.base.infra.employee.repository.impl;

import com.base.domain.employee.domain.Employee;
import com.base.domain.employee.domain.valueobjects.EmployeeId;
import com.base.domain.employee.repository.EmployeeRepository;
import com.base.domain.user.domain.valueobjects.Email;
import com.base.infra.employee.entity.EmployeeEntity;
import com.base.infra.employee.mapper.EmployeeMapper;
import com.base.infra.employee.repository.JpaEmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

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
}
