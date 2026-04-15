package com.base.app.project.handler;

import com.base.app.project.dto.ProjectManagementSidebarDto;
import com.base.domain.project.repository.ProjectManagementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UpdateProjectHandler {

    private final ProjectManagementRepository projectManagementRepository;

    public ProjectManagementSidebarDto handle(final String projectId, final UpdateProjectCommand command) {
        final String id = projectId.strip();
        final String name = command.name().strip();
        return ProjectManagementSidebarDto.fromDomain(projectManagementRepository.updateProjectName(id, name));
    }
}
