package com.allancordeiro.admin.catalogue.application.category.retrieve.list;

import com.allancordeiro.admin.catalogue.domain.category.CategoryGateway;
import com.allancordeiro.admin.catalogue.domain.category.CategorySearchQuery;
import com.allancordeiro.admin.catalogue.domain.pagination.Pagination;

import java.util.Objects;

public class DefaultListCategoriesUseCase extends ListCategoriesUseCase{
    private final CategoryGateway categoryGateway;

    public DefaultListCategoriesUseCase(final CategoryGateway categoryGateway) {
        this.categoryGateway = Objects.requireNonNull(categoryGateway);
    }

    @Override
    public Pagination<CategoryListOutput> execute(final CategorySearchQuery query) {
        return this.categoryGateway.findAll(query)
                .map(CategoryListOutput::from);
    }
}
