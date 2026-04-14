package com.base.infra.department.repository;

import com.base.infra.department.entity.DepartmentEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

public interface JpaDepartmentRepository extends JpaRepository<DepartmentEntity, Integer> {

    Optional<DepartmentEntity> findByCode(String code);

    boolean existsByCode(String code);

    @Query(
            value = """
                    SELECT d FROM DepartmentEntity d
                    WHERE (:codePattern IS NULL OR LOWER(d.code) LIKE :codePattern ESCAPE '\\')
                      AND (:namePattern IS NULL OR LOWER(d.name) LIKE :namePattern ESCAPE '\\')
                      AND (:createdFrom IS NULL OR d.createdAt >= :createdFrom)
                      AND (:createdToExclusive IS NULL OR d.createdAt < :createdToExclusive)
                    ORDER BY d.id DESC
                    """,
            countQuery = """
                    SELECT COUNT(d.id) FROM DepartmentEntity d
                    WHERE (:codePattern IS NULL OR LOWER(d.code) LIKE :codePattern ESCAPE '\\')
                      AND (:namePattern IS NULL OR LOWER(d.name) LIKE :namePattern ESCAPE '\\')
                      AND (:createdFrom IS NULL OR d.createdAt >= :createdFrom)
                      AND (:createdToExclusive IS NULL OR d.createdAt < :createdToExclusive)
                    """)
    Page<DepartmentEntity> searchDepartments(
            @Param("codePattern") String codePattern,
            @Param("namePattern") String namePattern,
            @Param("createdFrom") LocalDateTime createdFrom,
            @Param("createdToExclusive") LocalDateTime createdToExclusive,
            Pageable pageable);
}
