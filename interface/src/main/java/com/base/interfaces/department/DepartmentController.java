package com.base.interfaces.department;

import com.base.app.department.dto.DepartmentListItemDto;
import com.base.app.department.handler.ListDepartmentsAdminHandler;
import com.base.domain.shared.PageResult;
import com.base.interfaces.shared.response.CommonResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
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
