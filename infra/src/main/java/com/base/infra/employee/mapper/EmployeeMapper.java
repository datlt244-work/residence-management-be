package com.base.infra.employee.mapper;

import com.base.domain.employee.domain.Employee;
import com.base.domain.user.domain.valueobjects.Email;
import com.base.infra.employee.entity.EmployeeEntity;
import org.springframework.stereotype.Component;

@Component
public class EmployeeMapper {

    public EmployeeEntity toEntity(Employee domain) {
        EmployeeEntity entity = new EmployeeEntity();
        if (domain.getId() != null) {
            entity.setId(Integer.parseInt(domain.getId()));
        }
        entity.setDepartmentId(domain.getDepartmentId());
        entity.setFullName(domain.getFullName());
        entity.setPhone(domain.getPhone());
        entity.setEmail(domain.getEmail() != null ? domain.getEmail().value() : null);
        entity.setPasswordHash(domain.getPasswordHash());
        entity.setRole(domain.getRole());
        entity.setIsActive(domain.getIsActive());
        return entity;
    }

    public Employee toDomain(EmployeeEntity entity) {
        Employee domain = new Employee();
        domain.setId(String.valueOf(entity.getId()));
        domain.setDepartmentId(entity.getDepartmentId());
        domain.setFullName(entity.getFullName());
        domain.setPhone(entity.getPhone());
        domain.setEmail(entity.getEmail() != null ? Email.of(entity.getEmail()) : null);
        domain.setPasswordHash(entity.getPasswordHash());
        domain.setRole(entity.getRole());
        domain.setIsActive(entity.getIsActive());
        domain.setCreatedAt(entity.getCreatedAt());
        domain.setUpdatedAt(entity.getUpdatedAt());
        return domain;
    }
}
