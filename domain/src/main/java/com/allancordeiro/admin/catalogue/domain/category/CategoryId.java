package com.allancordeiro.admin.catalogue.domain.category;

import com.allancordeiro.admin.catalogue.domain.Identifier;

import java.util.Objects;
import java.util.UUID;

public class CategoryId extends Identifier {

    private final String value;

    private CategoryId(String value) {
        Objects.requireNonNull(value);
        this.value = value;
    }

    public static CategoryId unique() {
        return CategoryId.from(UUID.randomUUID());
    }

    public static CategoryId from(final String id) {
        return new CategoryId(id);
    }

    public static CategoryId from(final UUID id) {
        return new CategoryId(id.toString().toLowerCase());
    }

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final CategoryId that = (CategoryId) o;
        return getValue().equals(that.getValue());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getValue());
    }
}
