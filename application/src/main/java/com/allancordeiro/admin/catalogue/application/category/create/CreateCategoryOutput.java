package com.allancordeiro.admin.catalogue.application.category.create;

import com.allancordeiro.admin.catalogue.domain.category.Category;
import com.allancordeiro.admin.catalogue.domain.category.CategoryId;

public record CreateCategoryOutput(String id) {

    public static CreateCategoryOutput from(final String id) {
        return new CreateCategoryOutput(id);
    }

    public static CreateCategoryOutput from(final Category category) {
        return new CreateCategoryOutput(category.getId().getValue());
    }
}
