package com.base.domain.apartmenttype.domain;

import com.base.domain.shared.BaseDomain;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ApartmentType extends BaseDomain {

    private String zoneId;
    private String code;
    private String name;
    private Integer displayOrder;

    public static ApartmentType createNew(
            final String zoneId,
            final String code,
            final String name,
            final Integer displayOrder) {
        ApartmentType t = new ApartmentType();
        t.setZoneId(zoneId);
        t.setCode(code);
        t.setName(name);
        t.setDisplayOrder(displayOrder != null ? displayOrder : 0);
        return t;
    }
}
