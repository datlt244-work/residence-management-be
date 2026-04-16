package com.base.app.apartmenttype.handler;

import com.base.domain.apartmenttype.domain.ApartmentType;
import com.base.domain.apartmenttype.repository.ApartmentTypeManagementRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CreateApartmentTypeHandler {

    private final ApartmentTypeManagementRepository apartmentTypeManagementRepository;

    @Transactional
    public ApartmentType handle(final CreateApartmentTypeCommand command) {
        final String zoneId = command.zoneId().strip();
        final String name = command.name().strip();
        final String codeRaw = command.code();
        final String code = codeRaw != null && !codeRaw.isBlank() ? codeRaw.strip() : null;

        if (apartmentTypeManagementRepository.existsByZoneIdAndName(zoneId, name)) {
            throw new IllegalArgumentException(
                    "Apartment type with this name already exists for this zone: " + name);
        }
        if (code != null && apartmentTypeManagementRepository.existsByZoneIdAndCode(zoneId, code)) {
            throw new IllegalArgumentException(
                    "Apartment type with this code already exists for this zone: " + code);
        }

        final Integer displayOrder = command.displayOrder();
        try {
            ApartmentType saved =
                    apartmentTypeManagementRepository.save(ApartmentType.createNew(zoneId, code, name, displayOrder));
            log.info(
                    "Apartment type created id={} zoneId={} name={}",
                    saved.getId(),
                    saved.getZoneId(),
                    saved.getName());
            return saved;
        } catch (DataIntegrityViolationException ex) {
            log.warn(
                    "Apartment type insert failed (likely duplicate): zoneId={} name={} code={}",
                    zoneId,
                    name,
                    code,
                    ex);
            throw new IllegalArgumentException("Apartment type already exists for this zone (name or code conflict).");
        }
    }
}
