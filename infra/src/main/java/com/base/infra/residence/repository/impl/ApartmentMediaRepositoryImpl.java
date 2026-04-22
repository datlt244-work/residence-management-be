package com.base.infra.residence.repository.impl;

import com.base.domain.apartment.domain.ApartmentMedia;
import com.base.domain.apartment.repository.ApartmentMediaRepository;
import com.base.infra.residence.entity.ApartmentEntity;
import com.base.infra.residence.entity.ApartmentMediaEntity;
import com.base.infra.residence.repository.JpaApartmentMediaRepository;
import com.base.infra.residence.repository.JpaApartmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ApartmentMediaRepositoryImpl implements ApartmentMediaRepository {

    private static final int MEDIA_TYPE_MAX = 20;

    private final JpaApartmentMediaRepository jpaApartmentMediaRepository;
    private final JpaApartmentRepository jpaApartmentRepository;

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

    @Override
    @Transactional
    public ApartmentMedia createForApartment(
            final String apartmentId,
            final String storageKey,
            final String mediaType,
            final String thumbnailStorageKey,
            final boolean primary,
            final Integer displayOrder) {
        final long pk = parseApartmentPk(apartmentId.strip());
        ApartmentEntity apartment =
                jpaApartmentRepository.findById(pk).orElseThrow(() -> new IllegalArgumentException("Apartment not found: " + apartmentId));
        if (apartment.getDeletedAt() != null) {
            throw new IllegalArgumentException("Apartment not found: " + apartmentId);
        }
        if (primary) {
            jpaApartmentMediaRepository.clearPrimaryForApartment(pk);
        }
        final int order;
        if (displayOrder != null) {
            order = displayOrder;
        } else {
            final Integer max = jpaApartmentMediaRepository.findMaxDisplayOrderByApartmentId(pk);
            order = (max == null ? -1 : max) + 1;
        }
        final String thumb =
                thumbnailStorageKey != null && !thumbnailStorageKey.isBlank() ? thumbnailStorageKey.strip() : null;

        ApartmentMediaEntity entity = new ApartmentMediaEntity();
        entity.setApartment(apartment);
        entity.setMediaType(truncateMediaType(mediaType));
        entity.setUrl(storageKey.strip());
        entity.setThumbnailUrl(thumb);
        entity.setPrimary(primary);
        entity.setDisplayOrder(order);
        jpaApartmentMediaRepository.save(entity);
        return toDomain(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ApartmentMedia> findActiveMediaById(final String mediaId) {
        final long pk = parseMediaPk(mediaId.strip());
        return jpaApartmentMediaRepository.findActiveById(pk).map(ApartmentMediaRepositoryImpl::toDomain);
    }

    @Override
    @Transactional
    public void deleteMediaById(final String mediaId) {
        final long pk = parseMediaPk(mediaId.strip());
        jpaApartmentMediaRepository.deleteById(pk);
        jpaApartmentMediaRepository.flush();
    }

    private static String truncateMediaType(final String mediaType) {
        final String raw = mediaType != null ? mediaType.strip() : "FILE";
        if (raw.length() <= MEDIA_TYPE_MAX) {
            return raw;
        }
        return raw.substring(0, MEDIA_TYPE_MAX);
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

    private static long parseMediaPk(final String id) {
        try {
            return Long.parseLong(id);
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("Invalid media id: " + id);
        }
    }
}
