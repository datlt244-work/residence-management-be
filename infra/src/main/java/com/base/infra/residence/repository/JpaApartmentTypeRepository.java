package com.base.infra.residence.repository;

import com.base.infra.residence.entity.ApartmentTypeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaApartmentTypeRepository extends JpaRepository<ApartmentTypeEntity, Integer> {

    boolean existsByZone_IdAndName(Integer zoneId, String name);

    boolean existsByZone_IdAndCode(Integer zoneId, String code);

    boolean existsByZone_IdAndNameAndIdNot(Integer zoneId, String name, Integer id);
}
