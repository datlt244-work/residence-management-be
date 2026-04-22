package com.base.domain.apartment.repository;

import com.base.domain.apartment.domain.ApartmentMedia;

import java.util.List;
import java.util.Optional;

public interface ApartmentMediaRepository {

    List<ApartmentMedia> listByApartmentId(String apartmentId);

    ApartmentMedia createForApartment(
            String apartmentId,
            String storageKey,
            String mediaType,
            String thumbnailStorageKey,
            boolean primary,
            Integer displayOrder);

    /** Media row whose apartment is not soft-deleted. */
    Optional<ApartmentMedia> findActiveMediaById(String mediaId);

    void deleteMediaById(String mediaId);
}
