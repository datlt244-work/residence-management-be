package com.base.infra.employee.repository;

import com.base.domain.employee.domain.EmployeeRole;
import com.base.infra.employee.entity.EmployeeEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface JpaEmployeeRepository extends JpaRepository<EmployeeEntity, Integer> {

    Optional<EmployeeEntity> findByEmail(String email);

    boolean existsByEmail(String email);

    boolean existsByDepartmentId(Integer departmentId);

    @Query(
            value = """
                    SELECT e FROM EmployeeEntity e
                    WHERE (:role IS NULL OR e.role = :role)
                      AND (:active IS NULL OR e.isActive = :active)
                      AND (
                           :searchPattern IS NULL
                           OR LOWER(COALESCE(e.fullName, '')) LIKE :searchPattern ESCAPE '\\'
                           OR LOWER(COALESCE(e.email, '')) LIKE :searchPattern ESCAPE '\\'
                           OR LOWER(CAST(e.id AS string)) LIKE :searchPattern ESCAPE '\\'
                           OR LOWER(COALESCE(e.phone, '')) LIKE :searchPattern ESCAPE '\\'
                      )
                    ORDER BY e.id DESC
                    """,
            countQuery = """
                    SELECT COUNT(e.id) FROM EmployeeEntity e
                    WHERE (:role IS NULL OR e.role = :role)
                      AND (:active IS NULL OR e.isActive = :active)
                      AND (
                           :searchPattern IS NULL
                           OR LOWER(COALESCE(e.fullName, '')) LIKE :searchPattern ESCAPE '\\'
                           OR LOWER(COALESCE(e.email, '')) LIKE :searchPattern ESCAPE '\\'
                           OR LOWER(CAST(e.id AS string)) LIKE :searchPattern ESCAPE '\\'
                           OR LOWER(COALESCE(e.phone, '')) LIKE :searchPattern ESCAPE '\\'
                      )
                    """)
    Page<EmployeeEntity> findEmployeesForAdmin(
            @Param("role") EmployeeRole role,
            @Param("active") Boolean active,
            @Param("searchPattern") String searchPattern,
            Pageable pageable);
}
