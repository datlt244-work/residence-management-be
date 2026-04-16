package com.base.app.apartment.handler;

import com.base.app.apartment.command.MoveApartmentsCommand;
import com.base.app.apartment.dto.MoveApartmentsResultDto;
import com.base.domain.apartment.repository.ApartmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MoveApartmentsHandler {

    private static final int MAX_BATCH = 500;

    private final ApartmentRepository apartmentRepository;

    @Transactional
    public MoveApartmentsResultDto handle(final MoveApartmentsCommand command) {
        List<String> ids = new ArrayList<>();
        for (String id : command.apartmentIds()) {
            if (id != null && !id.isBlank()) {
                ids.add(id.strip());
            }
        }
        if (ids.isEmpty()) {
            throw new IllegalArgumentException("At least one apartment id is required");
        }
        if (ids.size() > MAX_BATCH) {
            throw new IllegalArgumentException("Too many apartments in one request (max " + MAX_BATCH + ")");
        }

        int moved = apartmentRepository.moveApartmentsToZoneAndType(
                ids, command.targetZoneId().strip(), command.targetApartmentTypeId().strip());
        return new MoveApartmentsResultDto(moved);
    }
}
