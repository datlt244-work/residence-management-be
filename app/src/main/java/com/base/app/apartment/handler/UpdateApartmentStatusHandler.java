package com.base.app.apartment.handler;

import com.base.app.apartment.command.UpdateApartmentStatusCommand;
import com.base.app.apartment.dto.ApartmentListItemDto;
import com.base.domain.apartment.repository.ApartmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UpdateApartmentStatusHandler {

    private final ApartmentRepository apartmentRepository;

    @Transactional
    public ApartmentListItemDto handle(final String apartmentId, final UpdateApartmentStatusCommand command) {
        return ApartmentListItemDto.fromDomain(
                apartmentRepository.updateApartmentStatus(apartmentId.strip(), command.status()));
    }
}
