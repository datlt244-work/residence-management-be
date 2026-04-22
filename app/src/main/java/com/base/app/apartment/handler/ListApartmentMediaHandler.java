package com.base.app.apartment.handler;

import com.base.app.apartment.dto.ApartmentMediaItemDto;
import com.base.domain.apartment.repository.ApartmentMediaRepository;
import com.base.domain.apartment.repository.ApartmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ListApartmentMediaHandler {

    private final ApartmentRepository apartmentRepository;
    private final ApartmentMediaRepository apartmentMediaRepository;

    @Transactional(readOnly = true)
    public List<ApartmentMediaItemDto> handle(final String apartmentId) {
        final String id = apartmentId.strip();
        apartmentRepository.findApartmentById(id);
        return apartmentMediaRepository.listByApartmentId(id).stream().map(ApartmentMediaItemDto::fromDomain).toList();
    }
}
