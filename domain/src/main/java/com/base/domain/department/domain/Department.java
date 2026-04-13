package com.base.domain.department.domain;

import com.base.domain.shared.BaseDomain;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class Department extends BaseDomain {

    private String code;
    private String name;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static Department create(String code, String name) {
        Department department = new Department();
        department.setCode(code);
        department.setName(name);
        return department;
    }
}
