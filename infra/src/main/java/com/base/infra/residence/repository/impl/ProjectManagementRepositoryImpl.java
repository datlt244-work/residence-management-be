package com.base.infra.residence.repository.impl;

import com.base.domain.project.domain.Project;
import com.base.domain.project.domain.Project.ProjectApartmentType;
import com.base.domain.project.domain.Project.ProjectZone;
import com.base.domain.project.repository.ProjectManagementRepository;
import com.base.infra.residence.entity.ApartmentTypeEntity;
import com.base.infra.residence.entity.ProjectEntity;
import com.base.infra.residence.entity.ZoneEntity;
import com.base.infra.residence.repository.JpaProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Repository
@RequiredArgsConstructor
public class ProjectManagementRepositoryImpl implements ProjectManagementRepository {

    private final JpaProjectRepository jpaProjectRepository;

    @Override
    @Transactional(readOnly = true)
    public List<Project> listSidebarTree(final String nameSearch, final String statusFilter) {
        final String namePattern;
        if (nameSearch != null && !nameSearch.isBlank()) {
            final String escaped = escapeLikePattern(nameSearch.trim().toLowerCase(Locale.ROOT));
            namePattern = "%" + escaped + "%";
        } else {
            namePattern = null;
        }

        final String status =
                statusFilter != null && !statusFilter.isBlank() ? statusFilter.trim() : null;

        List<ProjectEntity> entities = jpaProjectRepository.findAllForManagementSidebar(namePattern, status);
        List<Project> out = new ArrayList<>(entities.size());
        for (ProjectEntity p : entities) {
            out.add(toDomain(p));
        }
        return out;
    }

    private static Project toDomain(final ProjectEntity p) {
        Project project = new Project();
        project.setId(String.valueOf(p.getId()));
        project.setCode(p.getCode());
        project.setName(p.getName());
        project.setStatus(p.getStatus());
        project.setDisplayOrder(p.getDisplayOrder());
        List<ProjectZone> zones = new ArrayList<>();
        if (p.getZones() != null) {
            for (ZoneEntity z : p.getZones()) {
                zones.add(toZone(z));
            }
        }
        project.setZones(zones);
        return project;
    }

    private static ProjectZone toZone(final ZoneEntity z) {
        List<ProjectApartmentType> types = new ArrayList<>();
        if (z.getApartmentTypes() != null) {
            for (ApartmentTypeEntity t : z.getApartmentTypes()) {
                types.add(new ProjectApartmentType(
                        String.valueOf(t.getId()), t.getCode(), t.getName(), t.getDisplayOrder()));
            }
        }
        return new ProjectZone(String.valueOf(z.getId()), z.getCode(), z.getName(), z.getDisplayOrder(), types);
    }

    private static String escapeLikePattern(final String raw) {
        return raw.replace("\\", "\\\\").replace("%", "\\%").replace("_", "\\_");
    }
}
