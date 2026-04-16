package com.base.interfaces.apartmenttype;

import com.base.app.apartmenttype.handler.CreateApartmentTypeCommand;
import com.base.app.apartmenttype.handler.CreateApartmentTypeHandler;
import com.base.app.apartmenttype.handler.UpdateApartmentTypeCommand;
import com.base.app.apartmenttype.handler.UpdateApartmentTypeHandler;
import com.base.domain.apartmenttype.domain.ApartmentType;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Validated
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Apartment types management", description = "Loại căn hộ under zones")
public class ApartmentTypeManagementController {

    private final CreateApartmentTypeHandler createApartmentTypeHandler;
    private final UpdateApartmentTypeHandler updateApartmentTypeHandler;

    @PostMapping("/apartment-types-management")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(
            summary = "Create apartment type (loại căn)",
            description =
                    "Creates an apartment type under a zone. Name must be unique per zone; optional code must be unique per zone when provided.")
    public ResponseEntity<CommonResponse<ApartmentType>> createApartmentType(
            @Valid @RequestBody final CreateApartmentTypeCommand command) {
        ApartmentType created = createApartmentTypeHandler.handle(command);
        return ResponseEntity.ok(CommonResponse.success("Apartment type created successfully", created));
    }

    @PutMapping("/apartment-types-management/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(
            summary = "Update apartment type (loại căn)",
            description = "Updates display name only; name must stay unique within the same zone.")
    public ResponseEntity<CommonResponse<ApartmentType>> updateApartmentType(
            @Parameter(description = "Apartment type id") @PathVariable final String id,
            @Valid @RequestBody final UpdateApartmentTypeCommand command) {
        ApartmentType updated = updateApartmentTypeHandler.handle(id, command);
        return ResponseEntity.ok(CommonResponse.success("Apartment type updated successfully", updated));
    }
}
