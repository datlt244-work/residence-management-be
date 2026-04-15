package com.base.infra.residence.repository;

import com.base.infra.residence.entity.ProjectEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface JpaProjectRepository extends JpaRepository<ProjectEntity, Integer> {

    boolean existsByCode(String code);

    @Query(
            """
            SELECT DISTINCT p FROM ProjectEntity p
            LEFT JOIN FETCH p.zones z
            LEFT JOIN FETCH z.apartmentTypes t
            WHERE (:namePattern IS NULL OR LOWER(p.name) LIKE :namePattern ESCAPE '\\')
            AND (:status IS NULL OR p.status = :status)
            ORDER BY p.displayOrder ASC, p.id ASC
            """)
    List<ProjectEntity> findAllForManagementSidebar(
            @Param("namePattern") String namePattern, @Param("status") String status);
}
