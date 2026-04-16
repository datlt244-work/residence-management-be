package com.base.infra.residence.repository.impl;

import com.base.domain.apartmenttype.domain.ApartmentType;
import com.base.domain.apartmenttype.repository.ApartmentTypeManagementRepository;
import com.base.infra.residence.entity.ApartmentTypeEntity;
import com.base.infra.residence.entity.ZoneEntity;
import com.base.infra.residence.repository.JpaApartmentRepository;
import com.base.infra.residence.repository.JpaApartmentTypeRepository;
import com.base.infra.residence.repository.JpaZoneRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ApartmentTypeManagementRepositoryImpl implements ApartmentTypeManagementRepository {

    private final JpaApartmentTypeRepository jpaApartmentTypeRepository;
    private final JpaZoneRepository jpaZoneRepository;
    private final JpaApartmentRepository jpaApartmentRepository;

    @Override
    public boolean existsByZoneIdAndName(final String zoneId, final String name) {
        return jpaApartmentTypeRepository.existsByZone_IdAndName(parseZoneId(zoneId), name);
    }

    @Override
    public boolean existsByZoneIdAndCode(final String zoneId, final String code) {
        return jpaApartmentTypeRepository.existsByZone_IdAndCode(parseZoneId(zoneId), code);
    }

    @Override
    public ApartmentType save(final ApartmentType domain) {
        final int zonePk = parseZoneId(domain.getZoneId());
        if (!jpaZoneRepository.existsById(zonePk)) {
            throw new IllegalArgumentException("Zone not found: " + domain.getZoneId());
        }
        ZoneEntity zoneRef = jpaZoneRepository.getReferenceById(zonePk);
        ApartmentTypeEntity entity = new ApartmentTypeEntity();
        entity.setZone(zoneRef);
        entity.setCode(domain.getCode());
        entity.setName(domain.getName());
        entity.setDisplayOrder(domain.getDisplayOrder() != null ? domain.getDisplayOrder() : 0);
        ApartmentTypeEntity saved = jpaApartmentTypeRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public ApartmentType updateApartmentTypeName(final String apartmentTypeId, final String newName) {
        final int pk = parseApartmentTypeId(apartmentTypeId);
        ApartmentTypeEntity entity = jpaApartmentTypeRepository
                .findById(pk)
                .orElseThrow(() -> new IllegalArgumentException("Apartment type not found: " + apartmentTypeId));
        final int zoneId = entity.getZone().getId();
        if (jpaApartmentTypeRepository.existsByZone_IdAndNameAndIdNot(zoneId, newName, pk)) {
            throw new IllegalArgumentException(
                    "Apartment type with this name already exists for this zone: " + newName);
        }
        entity.setName(newName);
        ApartmentTypeEntity saved = jpaApartmentTypeRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public boolean existsById(final String apartmentTypeId) {
        return jpaApartmentTypeRepository.existsById(parseApartmentTypeId(apartmentTypeId));
    }

    @Override
    public boolean existsApartmentsForApartmentType(final String apartmentTypeId) {
        return jpaApartmentRepository.existsByApartmentType_Id(parseApartmentTypeId(apartmentTypeId));
    }

    @Override
    public void deleteById(final String apartmentTypeId) {
        final int pk = parseApartmentTypeId(apartmentTypeId);
        if (!jpaApartmentTypeRepository.existsById(pk)) {
            throw new IllegalArgumentException("Apartment type not found: " + apartmentTypeId);
        }
        jpaApartmentTypeRepository.deleteById(pk);
    }

    private static ApartmentType toDomain(final ApartmentTypeEntity e) {
        ApartmentType t = new ApartmentType();
        t.setId(String.valueOf(e.getId()));
        t.setZoneId(String.valueOf(e.getZone().getId()));
        t.setCode(e.getCode());
        t.setName(e.getName());
        t.setDisplayOrder(e.getDisplayOrder());
        return t;
    }

    private static int parseZoneId(final String id) {
        try {
            return Integer.parseInt(id.strip());
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("Invalid zone id: " + id);
        }
    }

    private static int parseApartmentTypeId(final String id) {
        try {
            return Integer.parseInt(id.strip());
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("Invalid apartment type id: " + id);
        }
    }
}
