package com.base.domain.shared;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class BaseDomain {

    private String id;

    protected BaseDomain() {}

    // Pure identity methods for domain logic
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BaseDomain that = (BaseDomain) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{id='" + id + "'}";
    }
}