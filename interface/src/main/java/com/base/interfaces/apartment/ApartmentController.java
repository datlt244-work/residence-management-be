package com.base.interfaces.apartment;

import com.base.app.apartment.dto.ApartmentListItemDto;
import com.base.app.apartment.dto.MoveApartmentsResultDto;
import com.base.app.apartment.handler.ListApartmentsHandler;
import com.base.app.apartment.handler.MoveApartmentsHandler;
import com.base.interfaces.apartment.request.MoveApartmentsRequest;
import com.base.domain.shared.PageResult;
import com.base.interfaces.shared.response.CommonResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Validated
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Apartments", description = "Apartment inventory and search")
public class ApartmentController {

    private final ListApartmentsHandler listApartmentsHandler;
    private final MoveApartmentsHandler moveApartmentsHandler;

    @GetMapping("/apartments")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'STAFF')")
    @Operation(
            summary = "List apartments",
            description =
                    "Paged list of non-deleted apartments. Optional filters: project, zone, apartment type; "
                            + "optional partial search on apartment code (case-insensitive). "
                            + "Any authenticated user (ADMIN, MANAGER, STAFF).")
    public ResponseEntity<CommonResponse<PageResult<ApartmentListItemDto>>> listApartments(
            @Parameter(description = "Zero-based page index") @RequestParam(defaultValue = "0") @Min(0) final int page,
            @Parameter(description = "Page size (max 100)") @RequestParam(defaultValue = "20") @Min(1) @Max(100)
                    final int size,
            @Parameter(description = "Filter by project id") @RequestParam(required = false) final String projectId,
            @Parameter(description = "Filter by zone (phân khu) id") @RequestParam(required = false) final String zoneId,
            @Parameter(description = "Filter by apartment type id") @RequestParam(required = false)
                    final String apartmentTypeId,
            @Parameter(description = "Partial match on apartment code (case-insensitive)")
            @RequestParam(required = false)
                    final String search) {

        PageResult<ApartmentListItemDto> data =
                listApartmentsHandler.handle(page, size, projectId, zoneId, apartmentTypeId, search);
        return ResponseEntity.ok(CommonResponse.success(data));
    }

    @PostMapping("/apartments/move")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(
            summary = "Move apartments to another zone / apartment type",
            description =
                    "Updates selected apartments to the target zone and apartment type. "
                            + "Target apartment type must belong to the target zone; project is set from that zone. "
                            + "Soft-deleted apartments cannot be moved.")
    public ResponseEntity<CommonResponse<MoveApartmentsResultDto>> moveApartments(
            @Valid @RequestBody final MoveApartmentsRequest request) {
        MoveApartmentsResultDto result = moveApartmentsHandler.handle(request.toCommand());
        return ResponseEntity.ok(CommonResponse.success("Apartments moved successfully", result));
    }
}
