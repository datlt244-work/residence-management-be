package com.base.interfaces.employee;

import com.base.app.admin.dto.AdminEmployeeDto;
import com.base.app.admin.handler.AdminEmployeeHandler;
import com.base.app.auth.handler.ChangePasswordHandler;
import com.base.app.employee.dto.EmployeeDto;
import com.base.app.employee.handler.CreateEmployeeHandler;
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
import org.springframework.web.bind.annotation.GetMapping;
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

    @GetMapping("/employees-management")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "List employees (admin)",
            tags = "Admin",
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

    @PostMapping("/employees-management")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create employee", tags = "Admin")
    public ResponseEntity<CommonResponse<EmployeeDto>> createEmployee(
            @Valid @RequestBody CreateEmployeeRequest request) {
        EmployeeDto dto = createEmployeeHandler.handle(request.toCommand());
        return ResponseEntity.ok(CommonResponse.success("Employee registered successfully", dto));
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
