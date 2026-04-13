package com.base.interfaces.employee;

import com.base.app.auth.handler.ChangePasswordHandler;
import com.base.app.employee.dto.EmployeeDto;
import com.base.app.employee.handler.UpdateEmployeeProfileHandler;
import com.base.domain.employee.domain.valueobjects.EmployeeId;
import com.base.domain.employee.repository.EmployeeRepository;
import com.base.infra.employee.entity.EmployeeEntity;
import com.base.interfaces.employee.request.ChangePasswordRequest;
import com.base.interfaces.employee.request.UpdateEmployeeProfileRequest;
import com.base.interfaces.shared.response.CommonResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/employees")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Employees", description = "Authenticated employee profile")
public class EmployeeController {

    private final EmployeeRepository employeeRepository;
    private final UpdateEmployeeProfileHandler updateEmployeeProfileHandler;
    private final ChangePasswordHandler changePasswordHandler;

    @GetMapping("/me")
    @Operation(summary = "Get current employee")
    public ResponseEntity<CommonResponse<EmployeeDto>> getCurrentEmployee(Authentication authentication) {
        EmployeeEntity principal = (EmployeeEntity) authentication.getPrincipal();

        var employee = employeeRepository.findById(EmployeeId.of(String.valueOf(principal.getId())))
                .orElseThrow(() -> new IllegalStateException("Employee not found"));

        return ResponseEntity.ok(CommonResponse.success(EmployeeDto.fromDomain(employee)));
    }

    @PutMapping("/me/profile")
    @Operation(summary = "Update full name")
    public ResponseEntity<CommonResponse<EmployeeDto>> updateProfile(
            @Valid @RequestBody UpdateEmployeeProfileRequest request,
            Authentication authentication) {

        EmployeeEntity principal = (EmployeeEntity) authentication.getPrincipal();
        String employeeId = String.valueOf(principal.getId());

        EmployeeDto dto = updateEmployeeProfileHandler.handle(request.toCommand(employeeId));

        return ResponseEntity.ok(CommonResponse.success("Profile updated successfully", dto));
    }

    @PutMapping("/me/password")
    @Operation(summary = "Change password")
    public ResponseEntity<CommonResponse<Void>> changePassword(
            @Valid @RequestBody ChangePasswordRequest request,
            Authentication authentication) {

        EmployeeEntity principal = (EmployeeEntity) authentication.getPrincipal();

        changePasswordHandler.handle(request.toCommand(principal.getEmail()));

        return ResponseEntity.ok(CommonResponse.success("Password changed successfully", null));
    }
}
