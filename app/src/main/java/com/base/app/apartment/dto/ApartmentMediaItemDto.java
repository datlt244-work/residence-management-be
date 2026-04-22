package com.base.app.apartment.dto;

import com.base.domain.apartment.ApartmentMediaUrlSigning;
import com.base.domain.apartment.domain.ApartmentMedia;

import java.time.LocalDateTime;

public record ApartmentMediaItemDto(
        String id,
        String apartmentId,
        String mediaType,
        String url,
        String thumbnailUrl,
        Boolean primary,
        Integer displayOrder,
        LocalDateTime createdAt) {

    public static ApartmentMediaItemDto fromDomain(
            final ApartmentMedia media, final ApartmentMediaUrlSigning urlSigning) {
        return new ApartmentMediaItemDto(
                media.getId(),
                media.getApartmentId(),
                media.getMediaType(),
                urlSigning.presignGetUrl(media.getUrl()),
                urlSigning.presignGetUrl(media.getThumbnailUrl()),
                media.getPrimary(),
                media.getDisplayOrder(),
                media.getCreatedAt());
    }
}
