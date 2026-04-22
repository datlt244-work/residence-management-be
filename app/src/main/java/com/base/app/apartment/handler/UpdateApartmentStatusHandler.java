package com.base.app.apartment.handler;

import com.base.app.apartment.command.UpdateApartmentStatusCommand;
import com.base.app.apartment.dto.ApartmentListItemDto;
import com.base.domain.apartment.ApartmentMediaUrlSigning;
import com.base.domain.apartment.repository.ApartmentMediaRepository;
import com.base.domain.apartment.repository.ApartmentRepository;
import lombok.RequiredArgsConstructor;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UpdateApartmentStatusHandler {

    private final ApartmentRepository apartmentRepository;
    private final ApartmentMediaRepository apartmentMediaRepository;
    private final ApartmentMediaUrlSigning apartmentMediaUrlSigning;

    @Transactional
    public ApartmentListItemDto handle(final String apartmentId, final UpdateApartmentStatusCommand command) {
        final String id = apartmentId.strip();
        final var updated = apartmentRepository.updateApartmentStatus(id, command.status());
        final String key =
                apartmentMediaRepository
                        .findPrimaryMediaStorageKeyByApartmentIds(List.of(id))
                        .get(id);
        return ApartmentListItemDto.fromDomain(updated, apartmentMediaUrlSigning.presignGetUrl(key));
    }
}
