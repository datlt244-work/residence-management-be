package com.base.app.apartment.dto;

import com.base.domain.apartment.domain.Apartment;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ApartmentAdminDto(
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
        String note,
        String ownerPhone,
        String ownerContact,
        String source,
        String status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt) {

    public static ApartmentAdminDto fromDomain(final Apartment apartment) {
        return new ApartmentAdminDto(
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
                apartment.getNote(),
                apartment.getOwnerPhone(),
                apartment.getOwnerContact(),
                apartment.getSource(),
                apartment.getStatus(),
                apartment.getCreatedAt(),
                apartment.getUpdatedAt());
    }
}
