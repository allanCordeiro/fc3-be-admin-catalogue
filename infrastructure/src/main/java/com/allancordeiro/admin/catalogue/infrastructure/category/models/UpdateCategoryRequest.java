package com.allancordeiro.admin.catalogue.infrastructure.category.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public record UpdateCategoryRequest(
        @JsonProperty("name") String name,
        @JsonProperty("description") String description,
        @JsonProperty("is_active") Boolean active) {
}