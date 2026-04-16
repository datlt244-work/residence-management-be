package com.base.domain.apartmenttype.repository;

import com.base.domain.apartmenttype.domain.ApartmentType;

public interface ApartmentTypeManagementRepository {

    boolean existsByZoneIdAndName(String zoneId, String name);

    boolean existsByZoneIdAndCode(String zoneId, String code);

    ApartmentType save(ApartmentType apartmentType);

    ApartmentType updateApartmentTypeName(String apartmentTypeId, String newName);

    boolean existsById(String apartmentTypeId);

    boolean existsApartmentsForApartmentType(String apartmentTypeId);

    void deleteById(String apartmentTypeId);
}
