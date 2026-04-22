package com.base.infra.residence.repository.impl;

import com.base.domain.apartment.domain.ApartmentMedia;
import com.base.domain.apartment.repository.ApartmentMediaRepository;
import com.base.infra.residence.entity.ApartmentMediaEntity;
import com.base.infra.residence.repository.JpaApartmentMediaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class ApartmentMediaRepositoryImpl implements ApartmentMediaRepository {

    private final JpaApartmentMediaRepository jpaApartmentMediaRepository;

    @Override
    @Transactional(readOnly = true)
    public List<ApartmentMedia> listByApartmentId(final String apartmentId) {
        final long pk = parseApartmentPk(apartmentId.strip());
        List<ApartmentMediaEntity> rows = jpaApartmentMediaRepository.listForActiveApartment(pk);
        List<ApartmentMedia> out = new ArrayList<>(rows.size());
        for (ApartmentMediaEntity e : rows) {
            out.add(toDomain(e));
        }
        return out;
    }

    private static ApartmentMedia toDomain(final ApartmentMediaEntity mediaEntity) {
        ApartmentMedia media = new ApartmentMedia();
        media.setId(String.valueOf(mediaEntity.getId()));
        media.setApartmentId(String.valueOf(mediaEntity.getApartment().getId()));
        media.setMediaType(mediaEntity.getMediaType());
        media.setUrl(mediaEntity.getUrl());
        media.setThumbnailUrl(mediaEntity.getThumbnailUrl());
        media.setPrimary(mediaEntity.getPrimary());
        media.setDisplayOrder(mediaEntity.getDisplayOrder());
        media.setCreatedAt(mediaEntity.getCreatedAt());
        return media;
    }

    private static long parseApartmentPk(final String id) {
        try {
            return Long.parseLong(id);
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("Invalid apartment id: " + id);
        }
    }
}
