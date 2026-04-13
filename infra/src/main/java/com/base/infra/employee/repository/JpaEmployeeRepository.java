package com.base.infra.employee.repository;

import com.base.infra.employee.entity.EmployeeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface JpaEmployeeRepository extends JpaRepository<EmployeeEntity, Integer> {

    Optional<EmployeeEntity> findByEmail(String email);

    boolean existsByEmail(String email);
}
