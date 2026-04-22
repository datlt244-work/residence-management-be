package com.base.domain.apartment.repository;

import com.base.domain.apartment.domain.ApartmentMedia;

import java.util.List;

public interface ApartmentMediaRepository {

    List<ApartmentMedia> listByApartmentId(String apartmentId);

    ApartmentMedia createForApartment(
            String apartmentId,
            String storageKey,
            String mediaType,
            String thumbnailStorageKey,
            boolean primary,
            Integer displayOrder);
}
