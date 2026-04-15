package com.base.domain.project.repository;

import com.base.domain.project.domain.Project;

import java.util.List;

public interface ProjectManagementRepository {
    List<Project> listSidebarTree(String nameSearch, String statusFilter);

    Project save(Project project);
}
