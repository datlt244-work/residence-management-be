package com.base.infra.department.mapper;

import com.base.domain.department.domain.Department;
import com.base.infra.department.entity.DepartmentEntity;
import org.springframework.stereotype.Component;

@Component
public class DepartmentMapper {

    public DepartmentEntity toEntity(Department domain) {
        DepartmentEntity entity = new DepartmentEntity();
        if (domain.getId() != null) {
            entity.setId(Integer.parseInt(domain.getId()));
        }
        entity.setCode(domain.getCode());
        entity.setName(domain.getName());
        return entity;
    }

    public Department toDomain(DepartmentEntity entity) {
        Department domain = new Department();
        domain.setId(String.valueOf(entity.getId()));
        domain.setCode(entity.getCode());
        domain.setName(entity.getName());
        domain.setCreatedAt(entity.getCreatedAt());
        domain.setUpdatedAt(entity.getUpdatedAt());
        return domain;
    }
}
