package com.base.app.zone.handler;

import com.base.domain.project.repository.ProjectManagementRepository;
import com.base.domain.zone.domain.Zone;
import com.base.domain.zone.repository.ZoneManagementRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CreateZoneHandler {

    private final ProjectManagementRepository projectManagementRepository;
    private final ZoneManagementRepository zoneManagementRepository;

    @Transactional
    public Zone handle(final CreateZoneCommand command) {
        final String projectId = command.projectId().strip();
        final String code = command.code().strip();
        final String name = command.name().strip();

        if (!projectManagementRepository.existsById(projectId)) {
            throw new IllegalArgumentException("Project not found: " + projectId);
        }
        if (zoneManagementRepository.existsByProjectIdAndCode(projectId, code)) {
            throw new IllegalArgumentException("Zone with this code already exists for this project: " + code);
        }

        final Integer displayOrder = command.displayOrder();
        try {
            Zone saved = zoneManagementRepository.save(Zone.createNew(projectId, code, name, displayOrder));
            log.info("Zone created id={} projectId={} code={}", saved.getId(), saved.getProjectId(), saved.getCode());
            return saved;
        } catch (DataIntegrityViolationException ex) {
            log.warn("Zone insert failed (likely duplicate code in project): projectId={} code={}", projectId, code, ex);
            throw new IllegalArgumentException("Zone with this code already exists for this project: " + code);
        }
    }
}
