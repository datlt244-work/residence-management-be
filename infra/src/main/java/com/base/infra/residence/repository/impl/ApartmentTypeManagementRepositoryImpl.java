package com.base.infra.residence.repository.impl;

import com.base.domain.apartmenttype.domain.ApartmentType;
import com.base.domain.apartmenttype.repository.ApartmentTypeManagementRepository;
import com.base.infra.residence.entity.ApartmentTypeEntity;
import com.base.infra.residence.entity.ZoneEntity;
import com.base.infra.residence.repository.JpaApartmentTypeRepository;
import com.base.infra.residence.repository.JpaZoneRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ApartmentTypeManagementRepositoryImpl implements ApartmentTypeManagementRepository {

    private final JpaApartmentTypeRepository jpaApartmentTypeRepository;
    private final JpaZoneRepository jpaZoneRepository;

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
}
