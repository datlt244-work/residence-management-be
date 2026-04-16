package com.base.domain.zone.repository;

import com.base.domain.zone.domain.Zone;

public interface ZoneManagementRepository {

    boolean existsByProjectIdAndCode(String projectId, String code);

    Zone save(Zone zone);

    Zone updateZoneName(String zoneId, String newName);
}
