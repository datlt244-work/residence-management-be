package com.base.domain.zone.domain;

import com.base.domain.shared.BaseDomain;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class Zone extends BaseDomain {

    private String projectId;
    private String code;
    private String name;
    private Integer displayOrder;

    public static Zone createNew(
            final String projectId, final String code, final String name, final Integer displayOrder) {
        Zone z = new Zone();
        z.setProjectId(projectId);
        z.setCode(code);
        z.setName(name);
        z.setDisplayOrder(displayOrder != null ? displayOrder : 0);
        return z;
    }
}
