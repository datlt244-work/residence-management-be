package com.base.interfaces.apartment;

import com.base.app.apartment.dto.ApartmentAdminDto;
import com.base.app.apartment.dto.ApartmentListItemDto;
import com.base.app.apartment.dto.ApartmentOwnerInfoDto;
import com.base.app.apartment.dto.BulkDeleteApartmentsResultDto;
import com.base.app.apartment.dto.MoveApartmentsResultDto;
import com.base.app.apartment.command.BulkDeleteApartmentsCommand;
import com.base.app.apartment.command.UpdateApartmentCommand;
import com.base.app.apartment.command.UpdateApartmentStatusCommand;
import com.base.app.apartment.handler.BulkDeleteApartmentsHandler;
import com.base.app.apartment.handler.GetApartmentDetailHandler;
import com.base.app.apartment.handler.GetApartmentOwnerInfoHandler;
import com.base.app.apartment.handler.ListApartmentsHandler;
import com.base.app.apartment.handler.MoveApartmentsHandler;
import com.base.app.apartment.handler.UpdateApartmentHandler;
import com.base.app.apartment.handler.UpdateApartmentStatusHandler;
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
    private final UpdateApartmentHandler updateApartmentHandler;
    private final GetApartmentOwnerInfoHandler getApartmentOwnerInfoHandler;
    private final BulkDeleteApartmentsHandler bulkDeleteApartmentsHandler;
    private final UpdateApartmentStatusHandler updateApartmentStatusHandler;
    private final GetApartmentDetailHandler getApartmentDetailHandler;

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

    @PatchMapping("/apartments/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'STAFF')")
    @Operation(
            summary = "Quick-update apartment status",
            description =
                    "Updates status only (e.g. switch to \"Đã cọc\"). "
                            + "Authenticated ADMIN, MANAGER, or STAFF. Soft-deleted apartments cannot be updated.")
    public ResponseEntity<CommonResponse<ApartmentListItemDto>> patchApartmentStatus(
            @Parameter(description = "Apartment id") @PathVariable final String id,
            @Valid @RequestBody final UpdateApartmentStatusCommand command) {
        ApartmentListItemDto dto = updateApartmentStatusHandler.handle(id, command);
        return ResponseEntity.ok(CommonResponse.success("Apartment status updated successfully", dto));
    }

    @GetMapping("/apartments/{id}/owner-info")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(
            summary = "Get owner phone and source (sensitive)",
            description = "Returns owner phone and listing source only. ADMIN and MANAGER only.")
    public ResponseEntity<CommonResponse<ApartmentOwnerInfoDto>> getApartmentOwnerInfo(
            @Parameter(description = "Apartment id") @PathVariable final String id) {
        ApartmentOwnerInfoDto dto = getApartmentOwnerInfoHandler.handle(id);
        return ResponseEntity.ok(CommonResponse.success(dto));
    }

    @GetMapping("/apartments/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'STAFF')")
    @Operation(
            summary = "Get apartment detail for forms",
            description =
                    "Full non-deleted apartment row. STAFF: ownerPhone, ownerContact, and source are null (masked). "
                            + "ADMIN and MANAGER receive all fields.")
    public ResponseEntity<CommonResponse<ApartmentAdminDto>> getApartmentDetail(
            @Parameter(description = "Apartment id") @PathVariable final String id) {
        ApartmentAdminDto dto = getApartmentDetailHandler.handle(id);
        return ResponseEntity.ok(CommonResponse.success(dto));
    }

    @PutMapping("/apartments/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(
            summary = "Update apartment details",
            description =
                    "Updates code, pricing, attributes, note, owner contact, source, and status. "
                            + "Does not change project / zone / apartment type (use POST /api/apartments/move). "
                            + "Apartment code must remain unique. Soft-deleted apartments cannot be updated.")
    public ResponseEntity<CommonResponse<ApartmentAdminDto>> updateApartment(
            @Parameter(description = "Apartment id") @PathVariable final String id,
            @Valid @RequestBody final UpdateApartmentCommand command) {
        ApartmentAdminDto dto = updateApartmentHandler.handle(id, command);
        return ResponseEntity.ok(CommonResponse.success("Apartment updated successfully", dto));
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

    @DeleteMapping("/apartments/bulk-delete")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(
            summary = "Bulk soft-delete apartments",
            description =
                    "Sets deletedAt on each selected apartment (soft delete). "
                    + "Request body: {\"apartmentIds\":[\"1\",\"2\"]}. "
                    + "Fails if any id is missing or already deleted. Max 500 ids per call. "
                    + "Some HTTP clients omit DELETE bodies; use a client that sends JSON.")
    public ResponseEntity<CommonResponse<BulkDeleteApartmentsResultDto>> bulkDeleteApartments(
            @Valid @RequestBody final BulkDeleteApartmentsCommand command) {
        BulkDeleteApartmentsResultDto result = bulkDeleteApartmentsHandler.handle(command);
        return ResponseEntity.ok(CommonResponse.success("Apartments deleted successfully", result));
    }
}
