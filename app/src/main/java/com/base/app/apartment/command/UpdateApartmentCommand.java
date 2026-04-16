package com.base.app.apartment.command;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record UpdateApartmentCommand(
        @NotBlank(message = "Apartment code is required") @Size(max = 50, message = "Code must not exceed 50 characters")
                String code,
        @NotNull(message = "Area is required")
                @DecimalMin(value = "0.0", inclusive = true, message = "Area must be non-negative")
                @Digits(integer = 6, fraction = 2, message = "Area must have at most 6 integer and 2 fraction digits")
                BigDecimal area,
        @NotNull(message = "Price is required")
                @DecimalMin(value = "0.0", inclusive = true, message = "Price must be non-negative")
                @Digits(integer = 15, fraction = 2, message = "Price must have at most 15 integer and 2 fraction digits")
                BigDecimal price,
        @DecimalMin(value = "0.0", inclusive = true, message = "Tax fee must be non-negative")
                @Digits(integer = 15, fraction = 2, message = "Tax fee must have at most 15 integer and 2 fraction digits")
                BigDecimal taxFee,
        @Size(max = 100, message = "Furniture status must not exceed 100 characters") String furnitureStatus,
        @Size(max = 100, message = "Legal status must not exceed 100 characters") String legalStatus,
        @Size(max = 50, message = "Balcony direction must not exceed 50 characters") String balconyDirection,
        @Size(max = 10000, message = "Note is too long") String note,
        @Size(max = 50, message = "Owner phone must not exceed 50 characters") String ownerPhone,
        @Size(max = 255, message = "Owner contact must not exceed 255 characters") String ownerContact,
        @Size(max = 150, message = "Source must not exceed 150 characters") String source,
        @NotBlank(message = "Status is required") @Size(max = 50, message = "Status must not exceed 50 characters")
                String status) {

    public UpdateApartmentCommand {
        if (code != null) {
            code = code.strip();
        }
        if (status != null) {
            status = status.strip();
        }
        furnitureStatus = blankToNull(furnitureStatus);
        legalStatus = blankToNull(legalStatus);
        balconyDirection = blankToNull(balconyDirection);
        note = blankToNull(note);
        ownerPhone = blankToNull(ownerPhone);
        ownerContact = blankToNull(ownerContact);
        source = blankToNull(source);
    }

    private static String blankToNull(final String s) {
        if (s == null) {
            return null;
        }
        final String t = s.strip();
        return t.isEmpty() ? null : t;
    }
}
