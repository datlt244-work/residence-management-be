package com.base.infra.residence.repository;

import com.base.infra.residence.entity.ApartmentEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface JpaApartmentRepository extends JpaRepository<ApartmentEntity, Long> {

    boolean existsByProject_Id(Integer projectId);

    boolean existsByZone_Id(Integer zoneId);

    boolean existsByApartmentType_Id(Integer apartmentTypeId);

    @Query(
            value =
                    """
                    SELECT DISTINCT a FROM ApartmentEntity a
                    JOIN FETCH a.project p
                    JOIN FETCH a.zone z
                    JOIN FETCH a.apartmentType t
                    WHERE a.deletedAt IS NULL
                    AND (:projectId IS NULL OR p.id = :projectId)
                    AND (:zoneId IS NULL OR z.id = :zoneId)
                    AND (:apartmentTypeId IS NULL OR t.id = :apartmentTypeId)
                    AND (:codePattern IS NULL OR LOWER(a.code) LIKE :codePattern ESCAPE '\\')
                    ORDER BY a.id DESC
                    """,
            countQuery =
                    """
                    SELECT COUNT(a.id) FROM ApartmentEntity a
                    WHERE a.deletedAt IS NULL
                    AND (:projectId IS NULL OR a.project.id = :projectId)
                    AND (:zoneId IS NULL OR a.zone.id = :zoneId)
                    AND (:apartmentTypeId IS NULL OR a.apartmentType.id = :apartmentTypeId)
                    AND (:codePattern IS NULL OR LOWER(a.code) LIKE :codePattern ESCAPE '\\')
                    """)
    Page<ApartmentEntity> searchApartments(
            @Param("projectId") Integer projectId,
            @Param("zoneId") Integer zoneId,
            @Param("apartmentTypeId") Integer apartmentTypeId,
            @Param("codePattern") String codePattern,
            Pageable pageable);
}
