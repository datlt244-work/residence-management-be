package com.base.interfaces.project;

import com.base.app.project.dto.ProjectManagementSidebarDto;
import com.base.app.project.handler.CreateProjectCommand;
import com.base.app.project.handler.CreateProjectHandler;
import com.base.app.project.handler.DeleteProjectAdminHandler;
import com.base.app.project.handler.ListProjectsManagementHandler;
import com.base.app.project.handler.SetProjectActiveCommand;
import com.base.app.project.handler.SetProjectActiveHandler;
import com.base.app.project.handler.UpdateProjectCommand;
import com.base.app.project.handler.UpdateProjectHandler;
import com.base.interfaces.shared.response.CommonResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Validated
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Projects management", description = "Project catalog for sidebar (project → zone → apartment type)")
public class ProjectManagementController {

    private final ListProjectsManagementHandler listProjectsManagementHandler;
    private final CreateProjectHandler createProjectHandler;
    private final UpdateProjectHandler updateProjectHandler;
    private final SetProjectActiveHandler setProjectActiveHandler;
    private final DeleteProjectAdminHandler deleteProjectAdminHandler;

    @GetMapping("/projects-management")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'STAFF')")
    @Operation(
            summary = "List projects for management sidebar",
            description =
                    "Tree: each project with zones (phân khu) and apartment types (loại căn). "
                            + "Optional search on project name (partial, case-insensitive); optional filter on project status (exact).")
    public ResponseEntity<CommonResponse<List<ProjectManagementSidebarDto>>> listProjectsManagement(
            @Parameter(description = "Partial match on project name (case-insensitive)")
            @RequestParam(required = false)
            final String name,
            @Parameter(description = "Exact match on project status (e.g. ACTIVE)")
            @RequestParam(required = false)
            final String status) {

        List<ProjectManagementSidebarDto> data = listProjectsManagementHandler.handle(name, status);
        return ResponseEntity.ok(CommonResponse.success(data));
    }

    @PostMapping("/projects-management")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Create project (admin)",
            description =
                    "Creates a project with display name and unique code. "
                            + "Status defaults to ACTIVE, displayOrder to 0.")
    public ResponseEntity<CommonResponse<ProjectManagementSidebarDto>> createProjectAdmin(
            @Valid @RequestBody final CreateProjectCommand command) {
        ProjectManagementSidebarDto dto = createProjectHandler.handle(command);
        return ResponseEntity.ok(CommonResponse.success("Project created successfully", dto));
    }

    @PutMapping("/projects-management/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update project (admin)", description = "Updates project display name only.")
    public ResponseEntity<CommonResponse<ProjectManagementSidebarDto>> updateProjectAdmin(
            @Parameter(description = "Project id") @PathVariable final String id,
            @Valid @RequestBody final UpdateProjectCommand command) {
        ProjectManagementSidebarDto dto = updateProjectHandler.handle(id, command);
        return ResponseEntity.ok(CommonResponse.success("Project updated successfully", dto));
    }

    @PatchMapping("/projects-management/{id}/active")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Set project active / inactive (admin)",
            description =
                    "Sets project status from body.active: true → ACTIVE, false → INACTIVE (stored in projects.status).")
    public ResponseEntity<CommonResponse<ProjectManagementSidebarDto>> setProjectActive(
            @Parameter(description = "Project id") @PathVariable final String id,
            @Valid @RequestBody final SetProjectActiveCommand command) {
        ProjectManagementSidebarDto dto = setProjectActiveHandler.handle(id, command);
        String message = Boolean.TRUE.equals(command.active())
                ? "Project activated successfully"
                : "Project deactivated successfully";
        return ResponseEntity.ok(CommonResponse.success(message, dto));
    }

    @DeleteMapping("/projects-management/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Delete project (admin)",
            description =
                    "Hard delete when the project has no apartments. Zones and apartment types cascade; "
                            + "apartments block delete until removed.")
    public ResponseEntity<CommonResponse<Void>> deleteProjectAdmin(
            @Parameter(description = "Project id") @PathVariable final String id) {
        deleteProjectAdminHandler.handle(id);
        return ResponseEntity.ok(CommonResponse.success("Project deleted successfully", null));
    }
}
