package com.base.domain.apartment.repository;

import com.base.domain.apartment.domain.ApartmentMedia;

import java.util.List;
import java.util.Map;
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

    /**
     * Marks this media as the primary listing image for its apartment; clears primary on other rows of the same
     * apartment. Media must belong to a non-deleted apartment.
     */
    ApartmentMedia setPrimaryMediaById(String mediaId);

    /**
     * For each apartment id, the {@code apartment_media.url} storage key of the row with {@code is_primary} true
     * (non-deleted apartment only). Apartments with no primary row are omitted.
     */
    Map<String, String> findPrimaryMediaStorageKeyByApartmentIds(List<String> apartmentIds);
}
