package com.base.interfaces.department;

import com.base.app.department.command.CreateDepartmentCommand;
import com.base.app.department.command.UpdateDepartmentAdminCommand;
import com.base.app.department.dto.DepartmentListItemDto;
import com.base.app.department.handler.CreateDepartmentHandler;
import com.base.app.department.handler.DeleteDepartmentAdminHandler;
import com.base.app.department.handler.GetDepartmentAdminDetailHandler;
import com.base.app.department.handler.ListDepartmentsAdminHandler;
import com.base.app.department.handler.UpdateDepartmentAdminHandler;
import com.base.domain.shared.PageResult;
import com.base.interfaces.shared.response.CommonResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Validated
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Departments", description = "Department catalog and admin APIs")
public class DepartmentController {

    private final ListDepartmentsAdminHandler listDepartmentsAdminHandler;
    private final GetDepartmentAdminDetailHandler getDepartmentAdminDetailHandler;
    private final UpdateDepartmentAdminHandler updateDepartmentAdminHandler;
    private final CreateDepartmentHandler createDepartmentHandler;
    private final DeleteDepartmentAdminHandler deleteDepartmentAdminHandler;

    @DeleteMapping("/departments-management/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Delete department (admin)",
            description = "Hard delete. Not allowed while any employee references this department.")
    public ResponseEntity<CommonResponse<Void>> deleteDepartmentAdmin(
            @Parameter(description = "Department id") @PathVariable final String id) {
        deleteDepartmentAdminHandler.handle(id);
        return ResponseEntity.ok(CommonResponse.success("Department deleted successfully", null));
    }

    @PostMapping("/departments-management")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create department (admin)", description = "Creates a department with unique code.")
    public ResponseEntity<CommonResponse<DepartmentListItemDto>> createDepartmentAdmin(
            @Valid @RequestBody CreateDepartmentCommand command) {
        DepartmentListItemDto dto = createDepartmentHandler.handle(command);
        return ResponseEntity.ok(CommonResponse.success("Department created successfully", dto));
    }

    @PutMapping("/departments-management/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update department (admin)", description = "Updates department code and name.")
    public ResponseEntity<CommonResponse<DepartmentListItemDto>> updateDepartmentAdmin(
            @Parameter(description = "Department id") @PathVariable final String id,
            @Valid @RequestBody UpdateDepartmentAdminCommand command) {
        DepartmentListItemDto dto = updateDepartmentAdminHandler.handle(id, command);
        return ResponseEntity.ok(CommonResponse.success("Department updated successfully", dto));
    }

    @GetMapping("/departments-management/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get department detail (admin)", description = "Returns department by id.")
    public ResponseEntity<CommonResponse<DepartmentListItemDto>> getDepartmentDetailAdmin(
            @Parameter(description = "Department id") @PathVariable final String id) {
        DepartmentListItemDto dto = getDepartmentAdminDetailHandler.handle(id);
        return ResponseEntity.ok(CommonResponse.success(dto));
    }

    @GetMapping("/departments-management")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "List departments (admin)",
            description = "Paged list. Optional partial match on code and name; optional createdAt date range (inclusive).")
    public ResponseEntity<CommonResponse<PageResult<DepartmentListItemDto>>> listDepartmentsAdmin(
            @Parameter(description = "Zero-based page index") @RequestParam(defaultValue = "0") @Min(0) final int page,
            @Parameter(description = "Page size (max 100)") @RequestParam(defaultValue = "20") @Min(1) @Max(100) final int size,
            @Parameter(description = "Partial match on department code (case-insensitive)")
            @RequestParam(required = false) final String code,
            @Parameter(description = "Partial match on department name (case-insensitive)")
            @RequestParam(required = false) final String name,
            @Parameter(description = "Filter created_at from this date (inclusive, ISO-8601 date)")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) final LocalDate createdFrom,
            @Parameter(description = "Filter created_at to this date (inclusive, ISO-8601 date)")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) final LocalDate createdTo) {

        PageResult<DepartmentListItemDto> data =
                listDepartmentsAdminHandler.handle(page, size, code, name, createdFrom, createdTo);
        return ResponseEntity.ok(CommonResponse.success(data));
    }
}
