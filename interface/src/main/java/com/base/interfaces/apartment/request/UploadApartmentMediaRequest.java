package com.base.interfaces.apartment.request;

import com.base.app.apartment.command.UploadApartmentMediaCommand;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public record UploadApartmentMediaRequest(
        @NotNull(message = "file is required") MultipartFile file,
        @Size(max = 20, message = "mediaType must be at most 20 characters") String mediaType,
        Boolean primary,
        Integer displayOrder) {

    public UploadApartmentMediaCommand toCommand(final String apartmentId) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("file part is required and must not be empty");
        }
        try {
            return new UploadApartmentMediaCommand(
                    apartmentId.strip(),
                    file.getBytes(),
                    file.getOriginalFilename(),
                    file.getContentType(),
                    mediaType,
                    primary != null && primary,
                    displayOrder);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to read uploaded file", e);
        }
    }
}
