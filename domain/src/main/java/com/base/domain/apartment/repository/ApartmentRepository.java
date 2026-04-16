package com.base.domain.apartment.repository;

import com.base.domain.apartment.domain.Apartment;
import com.base.domain.apartment.domain.ApartmentUpdate;
import com.base.domain.shared.PageResult;

import java.util.List;

public interface ApartmentRepository {

    PageResult<Apartment> searchApartments(
            Integer projectId,
            Integer zoneId,
            Integer apartmentTypeId,
            String codeSearch,
            int page,
            int size);

    int moveApartmentsToZoneAndType(List<String> apartmentIds, String targetZoneId, String targetApartmentTypeId);

    /** Updates scalar fields; rejects soft-deleted rows. Code must stay unique. */
    Apartment updateApartment(String apartmentId, ApartmentUpdate update);
}
