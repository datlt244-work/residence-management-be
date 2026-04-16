package com.base.infra.residence.repository.impl;

import com.base.domain.zone.domain.Zone;
import com.base.domain.zone.repository.ZoneManagementRepository;
import com.base.infra.residence.entity.ProjectEntity;
import com.base.infra.residence.entity.ZoneEntity;
import com.base.infra.residence.repository.JpaProjectRepository;
import com.base.infra.residence.repository.JpaZoneRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ZoneManagementRepositoryImpl implements ZoneManagementRepository {

    private final JpaZoneRepository jpaZoneRepository;
    private final JpaProjectRepository jpaProjectRepository;

    @Override
    public boolean existsByProjectIdAndCode(final String projectId, final String code) {
        return jpaZoneRepository.existsByProject_IdAndCode(parseProjectId(projectId), code);
    }

    @Override
    public Zone save(final Zone domain) {
        final int projectPk = parseProjectId(domain.getProjectId());
        if (!jpaProjectRepository.existsById(projectPk)) {
            throw new IllegalArgumentException("Project not found: " + domain.getProjectId());
        }
        ProjectEntity projectRef = jpaProjectRepository.getReferenceById(projectPk);
        ZoneEntity entity = new ZoneEntity();
        entity.setProject(projectRef);
        entity.setCode(domain.getCode());
        entity.setName(domain.getName());
        entity.setDisplayOrder(domain.getDisplayOrder() != null ? domain.getDisplayOrder() : 0);
        ZoneEntity saved = jpaZoneRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Zone updateZoneName(final String zoneId, final String newName) {
        final int pk = parseZoneId(zoneId);
        ZoneEntity entity = jpaZoneRepository
                .findById(pk)
                .orElseThrow(() -> new IllegalArgumentException("Zone not found: " + zoneId));
        entity.setName(newName);
        ZoneEntity saved = jpaZoneRepository.save(entity);
        return toDomain(saved);
    }

    private static Zone toDomain(final ZoneEntity e) {
        Zone z = new Zone();
        z.setId(String.valueOf(e.getId()));
        z.setProjectId(String.valueOf(e.getProject().getId()));
        z.setCode(e.getCode());
        z.setName(e.getName());
        z.setDisplayOrder(e.getDisplayOrder());
        return z;
    }

    private static int parseProjectId(final String id) {
        try {
            return Integer.parseInt(id.strip());
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("Invalid project id: " + id);
        }
    }

    private static int parseZoneId(final String id) {
        try {
            return Integer.parseInt(id.strip());
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("Invalid zone id: " + id);
        }
    }
}
