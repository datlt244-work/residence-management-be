package com.base.app.project.dto;

import com.base.domain.project.domain.Project;
import com.base.domain.project.domain.Project.ProjectApartmentType;
import com.base.domain.project.domain.Project.ProjectZone;

import java.util.List;

public record ProjectManagementSidebarDto(
        String id,
        String code,
        String name,
        String status,
        Integer displayOrder,
        List<ZoneSidebarDto> zones) {

    public static ProjectManagementSidebarDto fromDomain(final Project project) {
        List<ZoneSidebarDto> zoneDtos =
                project.getZones() == null
                        ? List.of()
                        : project.getZones().stream().map(ZoneSidebarDto::fromDomain).toList();
        return new ProjectManagementSidebarDto(
                project.getId(),
                project.getCode(),
                project.getName(),
                project.getStatus(),
                project.getDisplayOrder(),
                zoneDtos);
    }

    public record ZoneSidebarDto(
            String id, String code, String name, Integer displayOrder, List<ApartmentTypeSidebarDto> apartmentTypes) {

        static ZoneSidebarDto fromDomain(final ProjectZone zone) {
            List<ApartmentTypeSidebarDto> types =
                    zone.apartmentTypes() == null
                            ? List.of()
                            : zone.apartmentTypes().stream()
                                    .map(ApartmentTypeSidebarDto::fromDomain)
                                    .toList();
            return new ZoneSidebarDto(zone.id(), zone.code(), zone.name(), zone.displayOrder(), types);
        }
    }

    public record ApartmentTypeSidebarDto(String id, String code, String name, Integer displayOrder) {

        static ApartmentTypeSidebarDto fromDomain(final ProjectApartmentType t) {
            return new ApartmentTypeSidebarDto(t.id(), t.code(), t.name(), t.displayOrder());
        }
    }
}
