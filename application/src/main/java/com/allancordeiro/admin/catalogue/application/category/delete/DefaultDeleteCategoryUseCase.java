package com.allancordeiro.admin.catalogue.application.category.delete;

import com.allancordeiro.admin.catalogue.domain.category.CategoryGateway;
import com.allancordeiro.admin.catalogue.domain.category.CategoryId;

import java.util.Objects;

public class DefaultDeleteCategoryUseCase extends DeleteCategoryUseCase {

    private final CategoryGateway categoryGateway;

    public DefaultDeleteCategoryUseCase(final CategoryGateway categoryGateway) {
        this.categoryGateway = Objects.requireNonNull(categoryGateway);
    }

    @Override
    public void execute(String id) {
        this.categoryGateway.deleteById(CategoryId.from(id));
    }
}
