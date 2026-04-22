package com.base.app.apartment.handler;

import com.base.app.apartment.dto.ApartmentMediaItemDto;
import com.base.domain.apartment.ApartmentMediaUrlSigning;
import com.base.domain.apartment.repository.ApartmentMediaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SetApartmentMediaPrimaryHandler {

    private final ApartmentMediaRepository apartmentMediaRepository;
    private final ApartmentMediaUrlSigning apartmentMediaUrlSigning;

    @Transactional
    public ApartmentMediaItemDto handle(final String mediaId) {
        return ApartmentMediaItemDto.fromDomain(
                apartmentMediaRepository.setPrimaryMediaById(mediaId.strip()), apartmentMediaUrlSigning);
    }
}
