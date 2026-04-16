package com.base.infra.residence.repository;

import com.base.infra.residence.entity.ApartmentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaApartmentRepository extends JpaRepository<ApartmentEntity, Long> {

    boolean existsByProject_Id(Integer projectId);

    boolean existsByZone_Id(Integer zoneId);
}
