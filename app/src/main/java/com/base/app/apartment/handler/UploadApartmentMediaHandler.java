package com.base.app.apartment.handler;

import com.base.app.apartment.command.UploadApartmentMediaCommand;
import com.base.app.apartment.dto.ApartmentMediaItemDto;
import com.base.domain.apartment.ApartmentMediaObjectStorage;
import com.base.domain.apartment.ApartmentMediaUrlSigning;
import com.base.domain.apartment.domain.ApartmentMedia;
import com.base.domain.apartment.repository.ApartmentMediaRepository;
import com.base.domain.apartment.repository.ApartmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UploadApartmentMediaHandler {

    private final ApartmentMediaObjectStorage apartmentMediaObjectStorage;
    private final ApartmentRepository apartmentRepository;
    private final ApartmentMediaRepository apartmentMediaRepository;
    private final ApartmentMediaUrlSigning apartmentMediaUrlSigning;

    @Transactional
    public ApartmentMediaItemDto handle(final UploadApartmentMediaCommand command) {
        final String apartmentId = command.apartmentId().strip();
        apartmentRepository.findApartmentById(apartmentId);

        final String resolvedType = resolveMediaType(command.mediaType(), command.contentType());
        final String key =
                apartmentMediaObjectStorage.putObject(
                        apartmentId,
                        command.originalFilename(),
                        command.contentType(),
                        command.content());

        final ApartmentMedia created =
                apartmentMediaRepository.createForApartment(
                        apartmentId, key, resolvedType, null, command.primary(), command.displayOrder());
        return ApartmentMediaItemDto.fromDomain(created, apartmentMediaUrlSigning);
    }

    private static String resolveMediaType(final String requested, final String contentType) {
        if (requested != null && !requested.isBlank()) {
            return requested.strip();
        }
        final String ct = contentType != null ? contentType.strip().toLowerCase() : "";
        if (ct.startsWith("image/")) {
            return "IMAGE";
        }
        if (ct.startsWith("video/")) {
            return "VIDEO";
        }
        return "FILE";
    }
}
