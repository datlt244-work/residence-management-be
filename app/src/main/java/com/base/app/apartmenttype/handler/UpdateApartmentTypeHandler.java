package com.base.app.apartmenttype.handler;

import com.base.domain.apartmenttype.domain.ApartmentType;
import com.base.domain.apartmenttype.repository.ApartmentTypeManagementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UpdateApartmentTypeHandler {

    private final ApartmentTypeManagementRepository apartmentTypeManagementRepository;

    @Transactional
    public ApartmentType handle(final String apartmentTypeId, final UpdateApartmentTypeCommand command) {
        final String id = apartmentTypeId.strip();
        final String name = command.name().strip();
        return apartmentTypeManagementRepository.updateApartmentTypeName(id, name);
    }
}
