package com.base.interfaces.zone;

import com.base.app.zone.handler.CreateZoneCommand;
import com.base.app.zone.handler.CreateZoneHandler;
import com.base.domain.zone.domain.Zone;
import com.base.interfaces.shared.response.CommonResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Validated
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Zones management", description = "Phân khu (zones) under projects")
public class ZoneManagementController {

    private final CreateZoneHandler createZoneHandler;

    @PostMapping("/zones-management")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(
            summary = "Create zone (phân khu)",
            description = "Creates a zone under a project. Code must be unique within the same project.")
    public ResponseEntity<CommonResponse<Zone>> createZone(@Valid @RequestBody final CreateZoneCommand command) {
        Zone created = createZoneHandler.handle(command);
        return ResponseEntity.ok(CommonResponse.success("Zone created successfully", created));
    }
}
