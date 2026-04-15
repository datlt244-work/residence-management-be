package com.base.app.project.handler;

import com.base.app.project.dto.ProjectManagementSidebarDto;
import com.base.domain.project.repository.ProjectManagementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SetProjectActiveHandler {

    private static final String STATUS_ACTIVE = "ACTIVE";
    private static final String STATUS_INACTIVE = "INACTIVE";

    private final ProjectManagementRepository projectManagementRepository;

    @Transactional
    public ProjectManagementSidebarDto handle(final String projectId, final SetProjectActiveCommand command) {
        final String id = projectId.strip();
        final String status = Boolean.TRUE.equals(command.active()) ? STATUS_ACTIVE : STATUS_INACTIVE;
        return ProjectManagementSidebarDto.fromDomain(projectManagementRepository.updateProjectStatus(id, status));
    }
}
