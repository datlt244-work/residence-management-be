package com.base.domain.apartment.domain;

import com.base.domain.shared.BaseDomain;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class Apartment extends BaseDomain {

    private String projectId;
    private String projectCode;
    private String projectName;
    private String zoneId;
    private String zoneCode;
    private String zoneName;
    private String apartmentTypeId;
    private String apartmentTypeCode;
    private String apartmentTypeName;
    private String code;
    private BigDecimal area;
    private BigDecimal price;
    private BigDecimal taxFee;
    private String furnitureStatus;
    private String legalStatus;
    private String balconyDirection;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
