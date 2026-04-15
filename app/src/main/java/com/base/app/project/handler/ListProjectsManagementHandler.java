package com.base.app.project.handler;

import com.base.app.project.dto.ProjectManagementSidebarDto;
import com.base.domain.project.repository.ProjectManagementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ListProjectsManagementHandler {

    private final ProjectManagementRepository projectManagementRepository;

    @Transactional(readOnly = true)
    public List<ProjectManagementSidebarDto> handle(final String name, final String status) {
        return projectManagementRepository.listSidebarTree(name, status).stream()
                .map(ProjectManagementSidebarDto::fromDomain)
                .toList();
    }
}
