package com.base.app.apartment.handler;

import com.base.app.apartment.dto.ApartmentListItemDto;
import com.base.domain.apartment.repository.ApartmentRepository;
import com.base.domain.shared.PageResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ListApartmentsHandler {

    private static final int DEFAULT_SIZE = 20;
    private static final int MAX_SIZE = 100;

    private final ApartmentRepository apartmentRepository;

    @Transactional(readOnly = true)
    public PageResult<ApartmentListItemDto> handle(
            final int page,
            final int size,
            final String projectId,
            final String zoneId,
            final String apartmentTypeId,
            final String search) {

        final int normalizedSize = size <= 0 ? DEFAULT_SIZE : Math.min(size, MAX_SIZE);
        final int normalizedPage = Math.max(0, page);

        return apartmentRepository
                .searchApartments(
                        parseOptionalPositiveInt(projectId, "projectId"),
                        parseOptionalPositiveInt(zoneId, "zoneId"),
                        parseOptionalPositiveInt(apartmentTypeId, "apartmentTypeId"),
                        search,
                        normalizedPage,
                        normalizedSize)
                .map(ApartmentListItemDto::fromDomain);
    }

    private static Integer parseOptionalPositiveInt(final String raw, final String field) {
        if (raw == null || raw.isBlank()) {
            return null;
        }
        try {
            int value = Integer.parseInt(raw.strip());
            if (value <= 0) {
                throw new IllegalArgumentException("Invalid " + field + ": must be positive, got " + raw);
            }
            return value;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid " + field + ": " + raw);
        }
    }
}
