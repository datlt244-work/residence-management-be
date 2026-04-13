package com.base.infra.department.repository;

import com.base.infra.department.entity.DepartmentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface JpaDepartmentRepository extends JpaRepository<DepartmentEntity, Integer> {

    Optional<DepartmentEntity> findByCode(String code);

    boolean existsByCode(String code);
}
