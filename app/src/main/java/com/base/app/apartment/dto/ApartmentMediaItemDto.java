package com.base.app.apartment.dto;

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

    public static ApartmentMediaItemDto fromDomain(final ApartmentMedia media) {
        return new ApartmentMediaItemDto(
                media.getId(),
                media.getApartmentId(),
                media.getMediaType(),
                media.getUrl(),
                media.getThumbnailUrl(),
                media.getPrimary(),
                media.getDisplayOrder(),
                media.getCreatedAt());
    }
}
