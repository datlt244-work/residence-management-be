package com.base.interfaces.employee;

import com.base.app.admin.dto.AdminEmployeeDto;
import com.base.app.admin.handler.AdminEmployeeHandler;
import com.base.app.auth.handler.ChangePasswordHandler;
import com.base.app.employee.command.SetEmployeeActiveCommand;
import com.base.app.employee.command.UpdateEmployeeAdminCommand;
import com.base.app.employee.dto.EmployeeAdminDetailDto;
import com.base.app.employee.dto.EmployeeDto;
import com.base.app.employee.handler.CreateEmployeeHandler;
import com.base.app.employee.handler.DeleteEmployeeAdminHandler;
import com.base.app.employee.handler.GetEmployeeAdminDetailHandler;
import com.base.app.employee.handler.SetEmployeeActiveHandler;
import com.base.app.employee.handler.UpdateEmployeeAdminHandler;
import com.base.app.employee.handler.UpdateEmployeeProfileHandler;
import com.base.domain.employee.domain.valueobjects.EmployeeId;
import com.base.domain.employee.repository.EmployeeRepository;
import com.base.domain.shared.PageResult;
import com.base.infra.employee.entity.EmployeeEntity;
import com.base.interfaces.employee.request.ChangePasswordRequest;
import com.base.interfaces.employee.request.CreateEmployeeRequest;
import com.base.interfaces.employee.request.UpdateEmployeeProfileRequest;
import com.base.interfaces.shared.response.CommonResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
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

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Validated
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Employees", description = "Employee APIs: admin management and authenticated profile")
public class EmployeeController {

    private final EmployeeRepository employeeRepository;
    private final UpdateEmployeeProfileHandler updateEmployeeProfileHandler;
    private final ChangePasswordHandler changePasswordHandler;
    private final CreateEmployeeHandler createEmployeeHandler;
    private final AdminEmployeeHandler adminEmployeeHandler;
    private final GetEmployeeAdminDetailHandler getEmployeeAdminDetailHandler;
    private final SetEmployeeActiveHandler setEmployeeActiveHandler;
    private final UpdateEmployeeAdminHandler updateEmployeeAdminHandler;
    private final DeleteEmployeeAdminHandler deleteEmployeeAdminHandler;

    @GetMapping("/employees-management")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "List employees (admin)",
            description = "Paginated employees. Optional search, role, active.")
    public ResponseEntity<CommonResponse<PageResult<AdminEmployeeDto>>> listEmployeesAdmin(
            @Parameter(description = "Zero-based page index") @RequestParam(defaultValue = "0") @Min(0) final int page,
            @Parameter(description = "Page size (max 100)") @RequestParam(defaultValue = "20") @Min(1) @Max(100) final int size,
            @Parameter(description = "Search full name, email, phone, or employee id (partial match)")
            @RequestParam(required = false) final String search,
            @Parameter(description = "Filter by role: ADMIN, MANAGER, or STAFF") @RequestParam(required = false) final String role,
            @Parameter(description = "Filter by employee active flag") @RequestParam(required = false) final Boolean active) {

        PageResult<AdminEmployeeDto> data = adminEmployeeHandler.handle(page, size, search, role, active);
        return ResponseEntity.ok(CommonResponse.success(data));
    }

    @GetMapping("/employees-management/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Get employee detail (admin)",
            description = "Admin only: profile fields with department name when department exists.")
    public ResponseEntity<CommonResponse<EmployeeAdminDetailDto>> getEmployeeDetailAdmin(
            @Parameter(description = "Employee id") @PathVariable final String id) {
        EmployeeAdminDetailDto detail = getEmployeeAdminDetailHandler.handle(id);
        return ResponseEntity.ok(CommonResponse.success(detail));
    }

    @PostMapping("/employees-management")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create employee")
    public ResponseEntity<CommonResponse<EmployeeDto>> createEmployee(
            @Valid @RequestBody CreateEmployeeRequest request) {
        EmployeeDto dto = createEmployeeHandler.handle(request.toCommand());
        return ResponseEntity.ok(CommonResponse.success("Employee registered successfully", dto));
    }

    @PutMapping("/employees-management/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Update employee (admin)",
            description =
                    "Updates email, name, phone, role, department. To change password, send password and "
                            + "confirmPassword (same value); omit both to leave password unchanged.")
    public ResponseEntity<CommonResponse<EmployeeDto>> updateEmployeeAdmin(
            @Parameter(description = "Employee id") @PathVariable final String id,
            @Valid @RequestBody UpdateEmployeeAdminCommand command) {
        EmployeeDto dto = updateEmployeeAdminHandler.handle(id, command);
        return ResponseEntity.ok(CommonResponse.success("Employee updated successfully", dto));
    }

    @DeleteMapping("/employees-management/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Delete employee (admin)",
            description = "Hard delete. Fails if the employee is referenced (e.g. apartment created_by). "
                    + "Cannot delete your own account.")
    public ResponseEntity<CommonResponse<Void>> deleteEmployeeAdmin(
            @Parameter(description = "Employee id") @PathVariable final String id,
            Authentication authentication) {
        String actorId = null;
        if (authentication != null && authentication.getPrincipal() instanceof EmployeeEntity principal) {
            actorId = String.valueOf(principal.getId());
        }
        deleteEmployeeAdminHandler.handle(id, actorId);
        return ResponseEntity.ok(CommonResponse.success("Employee deleted successfully", null));
    }

    @PatchMapping("/employees-management/{id}/active")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Set employee active flag (admin)",
            description = "Sets is_active from body.active. Inactive employees cannot sign in.")
    public ResponseEntity<CommonResponse<EmployeeDto>> setEmployeeActive(
            @Parameter(description = "Employee id") @PathVariable final String id,
            @Valid @RequestBody SetEmployeeActiveCommand command) {
        EmployeeDto dto = setEmployeeActiveHandler.handle(id, command);
        String message = Boolean.TRUE.equals(command.active())
                ? "Employee activated successfully"
                : "Employee deactivated successfully";
        return ResponseEntity.ok(CommonResponse.success(message, dto));
    }

    @GetMapping("/employees/me")
    @Operation(summary = "Get current employee", tags = "Employees")
    public ResponseEntity<CommonResponse<EmployeeDto>> getCurrentEmployee(Authentication authentication) {
        EmployeeEntity principal = (EmployeeEntity) authentication.getPrincipal();

        var employee = employeeRepository.findById(EmployeeId.of(String.valueOf(principal.getId())))
                .orElseThrow(() -> new IllegalStateException("Employee not found"));

        return ResponseEntity.ok(CommonResponse.success(EmployeeDto.fromDomain(employee)));
    }

    @PutMapping("/employees/me/profile")
    @Operation(summary = "Update full name", tags = "Employees")
    public ResponseEntity<CommonResponse<EmployeeDto>> updateProfile(
            @Valid @RequestBody UpdateEmployeeProfileRequest request,
            Authentication authentication) {

        EmployeeEntity principal = (EmployeeEntity) authentication.getPrincipal();
        String employeeId = String.valueOf(principal.getId());

        EmployeeDto dto = updateEmployeeProfileHandler.handle(request.toCommand(employeeId));

        return ResponseEntity.ok(CommonResponse.success("Profile updated successfully", dto));
    }

    @PutMapping("/employees/me/password")
    @Operation(summary = "Change password", tags = "Employees")
    public ResponseEntity<CommonResponse<Void>> changePassword(
            @Valid @RequestBody ChangePasswordRequest request,
            Authentication authentication) {

        EmployeeEntity principal = (EmployeeEntity) authentication.getPrincipal();

        changePasswordHandler.handle(request.toCommand(principal.getEmail()));

        return ResponseEntity.ok(CommonResponse.success("Password changed successfully", null));
    }
}
