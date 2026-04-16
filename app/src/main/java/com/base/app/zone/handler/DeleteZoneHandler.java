package com.base.app.zone.handler;

import com.base.domain.zone.repository.ZoneManagementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DeleteZoneHandler {

    private final ZoneManagementRepository zoneManagementRepository;

    @Transactional
    public void handle(final String zoneId) {
        final String id = zoneId.strip();
        if (!zoneManagementRepository.existsById(id)) {
            throw new IllegalArgumentException("Zone not found: " + id);
        }
        if (zoneManagementRepository.existsApartmentsForZone(id)) {
            throw new IllegalArgumentException("Cannot delete zone: apartments still reference this zone");
        }
        zoneManagementRepository.deleteById(id);
    }
}
