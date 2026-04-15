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

    public static Project createNew(final String code, final String name) {
        Project project = new Project();
        project.setCode(code);
        project.setName(name);
        project.setStatus("ACTIVE");
        project.setDisplayOrder(0);
        project.setZones(new ArrayList<>());
        return project;
    }
}
