package com.base.app.apartmenttype.handler;

import com.base.domain.apartmenttype.repository.ApartmentTypeManagementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DeleteApartmentTypeHandler {

    private final ApartmentTypeManagementRepository apartmentTypeManagementRepository;

    @Transactional
    public void handle(final String apartmentTypeId) {
        final String id = apartmentTypeId.strip();
        if (!apartmentTypeManagementRepository.existsById(id)) {
            throw new IllegalArgumentException("Apartment type not found: " + id);
        }
        if (apartmentTypeManagementRepository.existsApartmentsForApartmentType(id)) {
            throw new IllegalArgumentException(
                    "Cannot delete apartment type: apartments still reference this apartment type");
        }
        apartmentTypeManagementRepository.deleteById(id);
    }
}
