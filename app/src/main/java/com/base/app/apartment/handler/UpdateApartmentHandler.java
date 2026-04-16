package com.base.app.apartment.handler;

import com.base.app.apartment.command.UpdateApartmentCommand;
import com.base.app.apartment.dto.ApartmentAdminDto;
import com.base.domain.apartment.domain.ApartmentUpdate;
import com.base.domain.apartment.repository.ApartmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class UpdateApartmentHandler {

    private final ApartmentRepository apartmentRepository;

    @Transactional
    public ApartmentAdminDto handle(final String apartmentId, final UpdateApartmentCommand command) {
        final BigDecimal taxFee = command.taxFee() != null ? command.taxFee() : BigDecimal.ZERO;
        ApartmentUpdate update =
                new ApartmentUpdate(
                        command.code(),
                        command.area(),
                        command.price(),
                        taxFee,
                        command.furnitureStatus(),
                        command.legalStatus(),
                        command.balconyDirection(),
                        command.note(),
                        command.ownerPhone(),
                        command.ownerContact(),
                        command.source(),
                        command.status());
        return ApartmentAdminDto.fromDomain(apartmentRepository.updateApartment(apartmentId.strip(), update));
    }
}
