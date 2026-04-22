package com.base.app.apartment.handler;

import com.base.domain.apartment.ApartmentMediaObjectStorage;
import com.base.domain.apartment.domain.ApartmentMedia;
import com.base.domain.apartment.repository.ApartmentMediaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DeleteApartmentMediaHandler {

    private final ApartmentMediaRepository apartmentMediaRepository;
    private final ApartmentMediaObjectStorage apartmentMediaObjectStorage;

    @Transactional
    public void handle(final String mediaId) {
        final String id = mediaId.strip();
        final ApartmentMedia media =
                apartmentMediaRepository
                        .findActiveMediaById(id)
                        .orElseThrow(() -> new IllegalArgumentException("Media not found: " + id));

        apartmentMediaRepository.deleteMediaById(id);

        apartmentMediaObjectStorage.deleteObject(media.getUrl());
        apartmentMediaObjectStorage.deleteObject(media.getThumbnailUrl());
    }
}
