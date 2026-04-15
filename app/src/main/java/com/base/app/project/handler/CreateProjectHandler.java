package com.base.app.project.handler;

import com.base.app.project.dto.ProjectManagementSidebarDto;
import com.base.domain.project.domain.Project;
import com.base.domain.project.repository.ProjectManagementRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class CreateProjectHandler {

    private final ProjectManagementRepository projectManagementRepository;

    public ProjectManagementSidebarDto handle(final CreateProjectCommand command) {
        final String name = command.name().strip();
        final String code = command.code().strip();
        try {
            Project saved = projectManagementRepository.save(Project.createNew(code, name));
            log.info("Project created id={} code={}", saved.getId(), saved.getCode());
            return ProjectManagementSidebarDto.fromDomain(saved);
        } catch (DataIntegrityViolationException ex) {
            log.warn("Project insert failed (likely duplicate code): {}", code, ex);
            throw new IllegalArgumentException("Project with this code already exists: " + code);
        }
    }
}
