package com.base.infra.residence.repository.impl;

import com.base.domain.project.domain.Project;
import com.base.domain.project.domain.Project.ProjectApartmentType;
import com.base.domain.project.domain.Project.ProjectZone;
import com.base.domain.project.repository.ProjectManagementRepository;
import com.base.infra.residence.entity.ApartmentTypeEntity;
import com.base.infra.residence.entity.ProjectEntity;
import com.base.infra.residence.entity.ZoneEntity;
import com.base.infra.residence.repository.JpaApartmentRepository;
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
    private final JpaApartmentRepository jpaApartmentRepository;

    @Override
    @Transactional
    public Project save(final Project project) {
        ProjectEntity entity = toNewEntity(project);
        ProjectEntity saved = jpaProjectRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Project updateProjectName(final String projectId, final String newName) {
        final int pk = parseProjectId(projectId);
        ProjectEntity entity = jpaProjectRepository
                .findById(pk)
                .orElseThrow(() -> new IllegalArgumentException("Project not found: " + projectId));
        entity.setName(newName);
        ProjectEntity saved = jpaProjectRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Project updateProjectStatus(final String projectId, final String status) {
        final int pk = parseProjectId(projectId);
        ProjectEntity entity = jpaProjectRepository
                .findById(pk)
                .orElseThrow(() -> new IllegalArgumentException("Project not found: " + projectId));
        entity.setStatus(status);
        ProjectEntity saved = jpaProjectRepository.save(entity);
        return toDomain(saved);
    }

    private static int parseProjectId(final String id) {
        try {
            return Integer.parseInt(id.strip());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid project id: " + id);
        }
    }

    @Override
    public boolean existsById(final String projectId) {
        return jpaProjectRepository.existsById(parseProjectId(projectId));
    }

    @Override
    public boolean existsApartmentsForProject(final String projectId) {
        return jpaApartmentRepository.existsByProject_Id(parseProjectId(projectId));
    }

    @Override
    public void deleteById(final String projectId) {
        final int pk = parseProjectId(projectId);
        if (!jpaProjectRepository.existsById(pk)) {
            throw new IllegalArgumentException("Project not found: " + projectId);
        }
        jpaProjectRepository.deleteById(pk);
    }

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

    private static ProjectEntity toNewEntity(final Project domain) {
        ProjectEntity e = new ProjectEntity();
        e.setCode(domain.getCode());
        e.setName(domain.getName());
        e.setStatus(domain.getStatus() != null ? domain.getStatus() : "ACTIVE");
        e.setDisplayOrder(domain.getDisplayOrder() != null ? domain.getDisplayOrder() : 0);
        return e;
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
