package com.base.app.apartment.dto;

import com.base.domain.apartment.domain.ApartmentOwnerInfo;

public record ApartmentOwnerInfoDto(String ownerPhone, String source) {

    public static ApartmentOwnerInfoDto fromDomain(final ApartmentOwnerInfo info) {
        return new ApartmentOwnerInfoDto(info.ownerPhone(), info.source());
    }
}
