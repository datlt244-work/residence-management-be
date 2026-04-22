package com.base.infra.residence.repository;

import com.base.infra.residence.entity.ApartmentMediaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface JpaApartmentMediaRepository extends JpaRepository<ApartmentMediaEntity, Long> {

    @Query(
            """
            SELECT m FROM ApartmentMediaEntity m
            JOIN m.apartment a
            WHERE a.id = :apartmentId AND a.deletedAt IS NULL
            ORDER BY m.displayOrder ASC, m.id ASC
            """)
    List<ApartmentMediaEntity> listForActiveApartment(@Param("apartmentId") Long apartmentId);
}
