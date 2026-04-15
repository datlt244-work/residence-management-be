package com.base.domain.project.domain;

import com.base.domain.shared.BaseDomain;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class Project extends BaseDomain {

    private String code;
    private String name;
    private String status;
    private Integer displayOrder;
    private List<ProjectZone> zones = new ArrayList<>();

    public record ProjectZone(
            String id, String code, String name, Integer displayOrder, List<ProjectApartmentType> apartmentTypes) {}

    public record ProjectApartmentType(String id, String code, String name, Integer displayOrder) {}
}
