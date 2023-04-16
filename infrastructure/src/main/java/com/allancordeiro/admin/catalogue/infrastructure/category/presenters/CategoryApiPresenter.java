package com.allancordeiro.admin.catalogue.infrastructure.category.presenters;

import com.allancordeiro.admin.catalogue.application.category.retrieve.get.CategoryOutput;
import com.allancordeiro.admin.catalogue.application.category.retrieve.list.CategoryListOutput;
import com.allancordeiro.admin.catalogue.infrastructure.category.models.CategoryResponse;
import com.allancordeiro.admin.catalogue.infrastructure.category.models.CategoryListResponse;

public interface CategoryApiPresenter {
    static CategoryResponse present(final CategoryOutput output) {
        return new CategoryResponse(
                output.id().getValue(),
                output.name(),
                output.description(),
                output.isActive(),
                output.createdAt(),
                output.updatedAt(),
                output.deletedAt()
        );
    }

    static CategoryListResponse present(final CategoryListOutput output) {
        return new CategoryListResponse(
                output.id().getValue(),
                output.name(),
                output.description(),
                output.isActive(),
                output.createdAt(),
                output.deletedAt()
        );
    }
}
