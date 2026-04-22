package com.base.infra.residence.repository;

import com.base.infra.residence.entity.ApartmentMediaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface JpaApartmentMediaRepository extends JpaRepository<ApartmentMediaEntity, Long> {

    @Query(
            """
            SELECT m FROM ApartmentMediaEntity m
            JOIN m.apartment a
            WHERE a.id = :apartmentId AND a.deletedAt IS NULL
            ORDER BY m.displayOrder ASC, m.id ASC
            """)
    List<ApartmentMediaEntity> listForActiveApartment(@Param("apartmentId") Long apartmentId);

    @Query(
            """
            SELECT m FROM ApartmentMediaEntity m
            JOIN FETCH m.apartment a
            WHERE m.id = :id AND a.deletedAt IS NULL
            """)
    Optional<ApartmentMediaEntity> findActiveById(@Param("id") Long id);

    @Query("SELECT COALESCE(MAX(m.displayOrder), -1) FROM ApartmentMediaEntity m WHERE m.apartment.id = :apartmentId")
    Integer findMaxDisplayOrderByApartmentId(@Param("apartmentId") Long apartmentId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE ApartmentMediaEntity m SET m.primary = false WHERE m.apartment.id = :apartmentId")
    void clearPrimaryForApartment(@Param("apartmentId") Long apartmentId);

    @Query(
            """
            SELECT m.apartment.id, m.url FROM ApartmentMediaEntity m
            JOIN m.apartment a
            WHERE a.deletedAt IS NULL AND m.primary = true AND m.apartment.id IN :apartmentIds
            ORDER BY m.id ASC
            """)
    List<Object[]> findPrimaryMediaApartmentIdAndUrl(@Param("apartmentIds") Collection<Long> apartmentIds);
}
