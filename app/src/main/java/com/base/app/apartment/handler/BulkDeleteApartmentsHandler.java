package com.base.app.apartment.handler;

import com.base.app.apartment.command.BulkDeleteApartmentsCommand;
import com.base.app.apartment.dto.BulkDeleteApartmentsResultDto;
import com.base.domain.apartment.repository.ApartmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BulkDeleteApartmentsHandler {

    private static final int MAX_BATCH = 500;

    private final ApartmentRepository apartmentRepository;

    @Transactional
    public BulkDeleteApartmentsResultDto handle(final BulkDeleteApartmentsCommand command) {
        LinkedHashSet<String> unique = new LinkedHashSet<>();
        for (String id : command.apartmentIds()) {
            if (id != null && !id.isBlank()) {
                unique.add(id.strip());
            }
        }
        List<String> ids = new ArrayList<>(unique);
        if (ids.isEmpty()) {
            throw new IllegalArgumentException("At least one apartment id is required");
        }
        if (ids.size() > MAX_BATCH) {
            throw new IllegalArgumentException("Too many apartments in one request (max " + MAX_BATCH + ")");
        }

        int deleted = apartmentRepository.bulkSoftDeleteApartments(ids);
        return new BulkDeleteApartmentsResultDto(deleted);
    }
}
