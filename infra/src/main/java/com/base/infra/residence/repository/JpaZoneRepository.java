package com.base.infra.residence.repository;

import com.base.infra.residence.entity.ZoneEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaZoneRepository extends JpaRepository<ZoneEntity, Integer> {

    boolean existsByProject_IdAndCode(Integer projectId, String code);
}
