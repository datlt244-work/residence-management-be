package com.base.app.apartment.command;

import java.util.Arrays;
import java.util.Objects;

public record UploadApartmentMediaCommand(
        String apartmentId,
        byte[] content,
        String originalFilename,
        String contentType,
        String mediaType,
        boolean primary,
        Integer displayOrder) {

    private static final int TO_STRING_CONTENT_PREVIEW = 64;

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final UploadApartmentMediaCommand that = (UploadApartmentMediaCommand) o;
        return primary == that.primary
                && Objects.equals(apartmentId, that.apartmentId)
                && Arrays.equals(content, that.content)
                && Objects.equals(originalFilename, that.originalFilename)
                && Objects.equals(contentType, that.contentType)
                && Objects.equals(mediaType, that.mediaType)
                && Objects.equals(displayOrder, that.displayOrder);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                apartmentId,
                content == null ? 0 : Arrays.hashCode(content),
                originalFilename,
                contentType,
                mediaType,
                primary,
                displayOrder);
    }

    @Override
    public String toString() {
        return "UploadApartmentMediaCommand[apartmentId="
                + apartmentId
                + ", content="
                + contentToString(content)
                + ", originalFilename="
                + originalFilename
                + ", contentType="
                + contentType
                + ", mediaType="
                + mediaType
                + ", primary="
                + primary
                + ", displayOrder="
                + displayOrder
                + ']';
    }

    private static String contentToString(final byte[] bytes) {
        if (bytes == null) {
            return "null";
        }
        if (bytes.length == 0) {
            return "[]";
        }
        if (bytes.length <= TO_STRING_CONTENT_PREVIEW) {
            return Arrays.toString(bytes);
        }
        return Arrays.toString(Arrays.copyOf(bytes, TO_STRING_CONTENT_PREVIEW))
                + ", ... ("
                + bytes.length
                + " bytes total)";
    }
}
