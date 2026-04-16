package com.base.app.zone.handler;

import com.base.domain.zone.domain.Zone;
import com.base.domain.zone.repository.ZoneManagementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UpdateZoneHandler {

    private final ZoneManagementRepository zoneManagementRepository;

    @Transactional
    public Zone handle(final String zoneId, final UpdateZoneCommand command) {
        final String id = zoneId.strip();
        final String name = command.name().strip();
        return zoneManagementRepository.updateZoneName(id, name);
    }
}
