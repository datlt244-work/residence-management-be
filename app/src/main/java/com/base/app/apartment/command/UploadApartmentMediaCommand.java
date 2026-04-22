package com.base.app.apartment.command;

/**
 * Upload bytes for a new apartment media row; {@code mediaType} may be blank to infer from {@code contentType}.
 */
public record UploadApartmentMediaCommand(
        String apartmentId,
        byte[] content,
        String originalFilename,
        String contentType,
        String mediaType,
        boolean primary,
        Integer displayOrder) {}
