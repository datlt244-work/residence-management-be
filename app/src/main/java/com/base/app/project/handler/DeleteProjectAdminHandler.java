package com.base.app.project.handler;

import com.base.domain.project.repository.ProjectManagementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DeleteProjectAdminHandler {

    private final ProjectManagementRepository projectManagementRepository;

    @Transactional
    public void handle(final String projectId) {
        final String id = projectId.strip();
        if (!projectManagementRepository.existsById(id)) {
            throw new IllegalArgumentException("Project not found: " + id);
        }
        if (projectManagementRepository.existsApartmentsForProject(id)) {
            throw new IllegalArgumentException("Cannot delete project: apartments still exist for this project");
        }
        projectManagementRepository.deleteById(id);
    }
}
