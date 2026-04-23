package com.base.interfaces.auth;

import com.base.app.auth.command.LoginEmailCommand;
import com.base.app.auth.dto.LoginResponseDto;
import com.base.app.auth.handler.EmployeeLoginHandler;
import com.base.interfaces.auth.request.LoginEmailRequest;
import com.base.interfaces.shared.response.CommonResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Sign-in")
public class AuthController {

    private final EmployeeLoginHandler employeeLoginHandler;

    @PostMapping("/login")
    @Operation(
            summary = "Employee sign-in",
            description = "Single login for ADMIN, MANAGER, and STAFF. JWT role claims drive access to protected APIs.")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Login successful",
                        content = @Content(schema = @Schema(implementation = CommonResponse.class))),
                @ApiResponse(responseCode = "401", description = "Invalid credentials or inactive account")
            })
    public ResponseEntity<CommonResponse<LoginResponseDto>> login(@Valid @RequestBody final LoginEmailRequest request) {
        try {
            final LoginEmailCommand command = new LoginEmailCommand(request.email(), request.password());
            final LoginResponseDto response = employeeLoginHandler.handle(command);
            return ResponseEntity.ok(CommonResponse.success("Login successful", response));
        } catch (final Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(CommonResponse.error("Login failed: " + e.getMessage()));
        }
    }
}
