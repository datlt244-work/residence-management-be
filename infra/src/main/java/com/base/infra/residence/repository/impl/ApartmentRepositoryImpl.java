package com.base.infra.residence.repository.impl;

import com.base.domain.apartment.domain.Apartment;
import com.base.domain.apartment.domain.ApartmentOwnerInfo;
import com.base.domain.apartment.domain.ApartmentUpdate;
import com.base.domain.apartment.repository.ApartmentRepository;
import com.base.domain.shared.PageResult;
import com.base.infra.residence.entity.ApartmentEntity;
import com.base.infra.residence.entity.ApartmentTypeEntity;
import com.base.infra.residence.entity.ProjectEntity;
import com.base.infra.residence.entity.ZoneEntity;
import com.base.infra.residence.repository.JpaApartmentRepository;
import com.base.infra.residence.repository.JpaApartmentTypeRepository;
import com.base.infra.residence.repository.JpaZoneRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

@Repository
@RequiredArgsConstructor
public class ApartmentRepositoryImpl implements ApartmentRepository {

    private static final int DEFAULT_SIZE = 20;
    private static final int MAX_SIZE = 100;

    private final JpaApartmentRepository jpaApartmentRepository;
    private final JpaZoneRepository jpaZoneRepository;
    private final JpaApartmentTypeRepository jpaApartmentTypeRepository;

    @Override
    @Transactional(readOnly = true)
    public PageResult<Apartment> searchApartments(
            final Integer projectId,
            final Integer zoneId,
            final Integer apartmentTypeId,
            final String codeSearch,
            final int page,
            final int size) {

        final int normalizedSize = size <= 0 ? DEFAULT_SIZE : Math.min(size, MAX_SIZE);
        final int normalizedPage = Math.max(0, page);

        final String codePattern;
        if (codeSearch != null && !codeSearch.isBlank()) {
            final String escaped = escapeLikePattern(codeSearch.trim().toLowerCase(Locale.ROOT));
            codePattern = "%" + escaped + "%";
        } else {
            codePattern = null;
        }

        Page<ApartmentEntity> pageResult = jpaApartmentRepository.searchApartments(
                projectId,
                zoneId,
                apartmentTypeId,
                codePattern,
                PageRequest.of(normalizedPage, normalizedSize));

        List<Apartment> content = new ArrayList<>(pageResult.getNumberOfElements());
        for (ApartmentEntity apartmentEntity : pageResult.getContent()) {
            content.add(toDomain(apartmentEntity));
        }

        return new PageResult<>(
                content,
                pageResult.getNumber(),
                pageResult.getSize(),
                pageResult.getTotalElements(),
                pageResult.getTotalPages());
    }

    @Override
    public int moveApartmentsToZoneAndType(
            final List<String> apartmentIds, final String targetZoneId, final String targetApartmentTypeId) {
        final int zonePk = parseIntId(targetZoneId, "zone");
        final int typePk = parseIntId(targetApartmentTypeId, "apartment type");

        ZoneEntity zone =
                jpaZoneRepository.findById(zonePk).orElseThrow(() -> new IllegalArgumentException("Zone not found: " + targetZoneId));
        ApartmentTypeEntity apartmentType = jpaApartmentTypeRepository
                .findById(typePk)
                .orElseThrow(() -> new IllegalArgumentException("Apartment type not found: " + targetApartmentTypeId));

        if (!Objects.equals(apartmentType.getZone().getId(), zone.getId())) {
            throw new IllegalArgumentException("Apartment type does not belong to the target zone");
        }

        ProjectEntity project = zone.getProject();

        int moved = 0;
        for (String rawId : apartmentIds) {
            if (rawId == null || rawId.isBlank()) {
                continue;
            }
            final String id = rawId.strip();
            final long apartmentPk = parseLongId(id, "apartment");
            ApartmentEntity entity = jpaApartmentRepository
                    .findById(apartmentPk)
                    .orElseThrow(() -> new IllegalArgumentException("Apartment not found: " + id));
            if (entity.getDeletedAt() != null) {
                throw new IllegalArgumentException("Cannot move deleted apartment: " + id);
            }
            entity.setProject(project);
            entity.setZone(zone);
            entity.setApartmentType(apartmentType);
            jpaApartmentRepository.save(entity);
            moved++;
        }
        return moved;
    }

    @Override
    public Apartment updateApartment(final String apartmentId, final ApartmentUpdate update) {
        final String id = apartmentId.strip();
        final long pk = parseLongId(id, "apartment");
        ApartmentEntity entity = jpaApartmentRepository
                .findById(pk)
                .orElseThrow(() -> new IllegalArgumentException("Apartment not found: " + id));
        if (entity.getDeletedAt() != null) {
            throw new IllegalArgumentException("Cannot update deleted apartment: " + id);
        }

        final String newCode = update.code().strip();
        if (!Objects.equals(newCode, entity.getCode()) && jpaApartmentRepository.existsByCodeAndIdIsNot(newCode, pk)) {
            throw new IllegalArgumentException("Apartment code already exists: " + newCode);
        }

        entity.setCode(newCode);
        entity.setArea(update.area());
        entity.setPrice(update.price());
        entity.setTaxFee(update.taxFee() != null ? update.taxFee() : BigDecimal.ZERO);
        entity.setFurnitureStatus(update.furnitureStatus());
        entity.setLegalStatus(update.legalStatus());
        entity.setBalconyDirection(update.balconyDirection());
        entity.setNote(update.note());
        entity.setOwnerPhone(update.ownerPhone());
        entity.setOwnerContact(update.ownerContact());
        entity.setSource(update.source());
        entity.setStatus(update.status().strip());

        jpaApartmentRepository.save(entity);
        return toDomain(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public ApartmentOwnerInfo getApartmentOwnerInfo(final String apartmentId) {
        final String id = apartmentId.strip();
        final long pk = parseLongId(id, "apartment");
        ApartmentEntity entity = jpaApartmentRepository
                .findById(pk)
                .orElseThrow(() -> new IllegalArgumentException("Apartment not found: " + id));
        if (entity.getDeletedAt() != null) {
            throw new IllegalArgumentException("Apartment not found: " + id);
        }
        return new ApartmentOwnerInfo(entity.getOwnerPhone(), entity.getSource());
    }

    private static Apartment toDomain(final ApartmentEntity apartmentEntity) {
        Apartment apartment = new Apartment();
        apartment.setId(String.valueOf(apartmentEntity.getId()));
        apartment.setProjectId(String.valueOf(apartmentEntity.getProject().getId()));
        apartment.setProjectCode(apartmentEntity.getProject().getCode());
        apartment.setProjectName(apartmentEntity.getProject().getName());
        apartment.setZoneId(String.valueOf(apartmentEntity.getZone().getId()));
        apartment.setZoneCode(apartmentEntity.getZone().getCode());
        apartment.setZoneName(apartmentEntity.getZone().getName());
        apartment.setApartmentTypeId(String.valueOf(apartmentEntity.getApartmentType().getId()));
        apartment.setApartmentTypeCode(apartmentEntity.getApartmentType().getCode());
        apartment.setApartmentTypeName(apartmentEntity.getApartmentType().getName());
        apartment.setCode(apartmentEntity.getCode());
        apartment.setArea(apartmentEntity.getArea());
        apartment.setPrice(apartmentEntity.getPrice());
        apartment.setTaxFee(apartmentEntity.getTaxFee());
        apartment.setFurnitureStatus(apartmentEntity.getFurnitureStatus());
        apartment.setLegalStatus(apartmentEntity.getLegalStatus());
        apartment.setBalconyDirection(apartmentEntity.getBalconyDirection());
        apartment.setNote(apartmentEntity.getNote());
        apartment.setOwnerPhone(apartmentEntity.getOwnerPhone());
        apartment.setOwnerContact(apartmentEntity.getOwnerContact());
        apartment.setSource(apartmentEntity.getSource());
        apartment.setStatus(apartmentEntity.getStatus());
        apartment.setCreatedAt(apartmentEntity.getCreatedAt());
        apartment.setUpdatedAt(apartmentEntity.getUpdatedAt());
        return apartment;
    }

    private static String escapeLikePattern(final String raw) {
        return raw.replace("\\", "\\\\").replace("%", "\\%").replace("_", "\\_");
    }

    private static int parseIntId(final String id, final String label) {
        try {
            return Integer.parseInt(id.strip());
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("Invalid " + label + " id: " + id);
        }
    }

    private static long parseLongId(final String id, final String label) {
        try {
            return Long.parseLong(id.strip());
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("Invalid " + label + " id: " + id);
        }
    }
}
