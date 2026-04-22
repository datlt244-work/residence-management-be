package com.base.domain.apartment;

public interface ApartmentMediaObjectStorage {

    String putObject(String apartmentId, String originalFilename, String contentType, byte[] content);

    void deleteObject(String storageKeyOrUrl);
}
