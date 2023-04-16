package com.allancordeiro.admin.catalogue.application.category.update;

import com.allancordeiro.admin.catalogue.domain.category.Category;
import com.allancordeiro.admin.catalogue.domain.category.CategoryId;

public record UpdateCategoryOutput(String id) {

    public static  UpdateCategoryOutput from(final String id) {
        return new UpdateCategoryOutput(id);
    }

    public static  UpdateCategoryOutput from(final Category category) {
        return new UpdateCategoryOutput(category.getId().getValue());
    }
}
