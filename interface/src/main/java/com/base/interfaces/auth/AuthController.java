package com.base.interfaces.auth;

import com.base.app.auth.command.LoginEmailCommand;
import com.base.app.auth.dto.LoginResponseDto;
import com.base.app.auth.handler.AdminLoginHandler;
import com.base.app.auth.handler.ManagerLoginHandler;
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

    private final AdminLoginHandler adminLoginHandler;
    private final ManagerLoginHandler managerLoginHandler;

    @PostMapping("/admin/login")
    @Operation(summary = "Admin sign-in")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login successful",
                    content = @Content(schema = @Schema(implementation = CommonResponse.class))),
            @ApiResponse(responseCode = "401", description = "Invalid credentials")
    })
    public ResponseEntity<CommonResponse<LoginResponseDto>> adminLogin(@Valid @RequestBody LoginEmailRequest request) {
        try {
            LoginEmailCommand command = new LoginEmailCommand(request.email(), request.password());
            LoginResponseDto response = adminLoginHandler.handle(command);
            return ResponseEntity.ok(CommonResponse.success("Admin login successful", response));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(CommonResponse.error("Admin login failed: " + e.getMessage()));
        }
    }

    @PostMapping("/manager/login")
    @Operation(summary = "Manager or staff sign-in")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login successful",
                    content = @Content(schema = @Schema(implementation = CommonResponse.class))),
            @ApiResponse(responseCode = "401", description = "Invalid credentials")
    })
    public ResponseEntity<CommonResponse<LoginResponseDto>> managerLogin(@Valid @RequestBody LoginEmailRequest request) {
        try {
            LoginEmailCommand command = new LoginEmailCommand(request.email(), request.password());
            LoginResponseDto response = managerLoginHandler.handle(command);
            return ResponseEntity.ok(CommonResponse.success("Manager login successful", response));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(CommonResponse.error("Manager login failed: " + e.getMessage()));
        }
    }
}
