package com.base.domain.apartment.domain;

import java.math.BigDecimal;

/** Mutable apartment attributes for admin update (not project/zone/type; use move API). */
public record ApartmentUpdate(
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
        String status) {}
