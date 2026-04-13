package com.base.interfaces.admin;

import com.base.app.employee.dto.EmployeeDto;
import com.base.app.employee.handler.CreateEmployeeHandler;
import com.base.interfaces.employee.request.CreateEmployeeRequest;
import com.base.interfaces.shared.response.CommonResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Admin", description = "ADMIN role only")
public class AdminController {

    private final CreateEmployeeHandler createEmployeeHandler;

    @PostMapping("/employees")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create employee")
    public ResponseEntity<CommonResponse<EmployeeDto>> createEmployee(
            @Valid @RequestBody CreateEmployeeRequest request) {
        EmployeeDto dto = createEmployeeHandler.handle(request.toCommand());
        return ResponseEntity.ok(CommonResponse.success("Employee registered successfully", dto));
    }
}
