package com.base.app.apartment.handler;

import com.base.app.apartment.dto.ApartmentOwnerInfoDto;
import com.base.domain.apartment.repository.ApartmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GetApartmentOwnerInfoHandler {

    private final ApartmentRepository apartmentRepository;

    @Transactional(readOnly = true)
    public ApartmentOwnerInfoDto handle(final String apartmentId) {
        return ApartmentOwnerInfoDto.fromDomain(apartmentRepository.getApartmentOwnerInfo(apartmentId));
    }
}
