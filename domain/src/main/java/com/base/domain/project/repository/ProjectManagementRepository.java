package com.base.domain.project.repository;

import com.base.domain.project.domain.Project;

import java.util.List;

public interface ProjectManagementRepository {
    List<Project> listSidebarTree(String nameSearch, String statusFilter);

    Project save(Project project);

    Project updateProjectName(String projectId, String newName);

    Project updateProjectStatus(String projectId, String status);

    boolean existsById(String projectId);

    boolean existsApartmentsForProject(String projectId);

    void deleteById(String projectId);
}
