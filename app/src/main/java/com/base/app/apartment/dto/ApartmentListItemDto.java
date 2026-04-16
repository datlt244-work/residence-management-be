package com.base.app.apartment.dto;

import com.base.domain.apartment.domain.Apartment;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ApartmentListItemDto(
        String id,
        String projectId,
        String projectCode,
        String projectName,
        String zoneId,
        String zoneCode,
        String zoneName,
        String apartmentTypeId,
        String apartmentTypeCode,
        String apartmentTypeName,
        String code,
        BigDecimal area,
        BigDecimal price,
        BigDecimal taxFee,
        String furnitureStatus,
        String legalStatus,
        String balconyDirection,
        String status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt) {

    public static ApartmentListItemDto fromDomain(final Apartment apartment) {
        return new ApartmentListItemDto(
                apartment.getId(),
                apartment.getProjectId(),
                apartment.getProjectCode(),
                apartment.getProjectName(),
                apartment.getZoneId(),
                apartment.getZoneCode(),
                apartment.getZoneName(),
                apartment.getApartmentTypeId(),
                apartment.getApartmentTypeCode(),
                apartment.getApartmentTypeName(),
                apartment.getCode(),
                apartment.getArea(),
                apartment.getPrice(),
                apartment.getTaxFee(),
                apartment.getFurnitureStatus(),
                apartment.getLegalStatus(),
                apartment.getBalconyDirection(),
                apartment.getStatus(),
                apartment.getCreatedAt(),
                apartment.getUpdatedAt());
    }
}
