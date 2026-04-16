package com.base.infra.residence.repository.impl;

import com.base.domain.apartment.domain.Apartment;
import com.base.domain.apartment.repository.ApartmentRepository;
import com.base.domain.shared.PageResult;
import com.base.infra.residence.entity.ApartmentEntity;
import com.base.infra.residence.repository.JpaApartmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Repository
@RequiredArgsConstructor
public class ApartmentRepositoryImpl implements ApartmentRepository {

    private static final int DEFAULT_SIZE = 20;
    private static final int MAX_SIZE = 100;

    private final JpaApartmentRepository jpaApartmentRepository;

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
        apartment.setStatus(apartmentEntity.getStatus());
        apartment.setCreatedAt(apartmentEntity.getCreatedAt());
        apartment.setUpdatedAt(apartmentEntity.getUpdatedAt());
        return apartment;
    }

    private static String escapeLikePattern(final String raw) {
        return raw.replace("\\", "\\\\").replace("%", "\\%").replace("_", "\\_");
    }
}
