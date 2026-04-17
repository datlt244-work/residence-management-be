package com.base.app.apartment.handler;

import com.base.app.apartment.dto.ApartmentAdminDto;
import com.base.app.security.CurrentEmployeeRoleSupport;
import com.base.domain.apartment.repository.ApartmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GetApartmentDetailHandler {

    private final ApartmentRepository apartmentRepository;

    @Transactional(readOnly = true)
    public ApartmentAdminDto handle(final String apartmentId) {
        return ApartmentAdminDto.fromDomainForViewer(
                apartmentRepository.findApartmentById(apartmentId.strip()),
                CurrentEmployeeRoleSupport.requireEmployeeRole());
    }
}
