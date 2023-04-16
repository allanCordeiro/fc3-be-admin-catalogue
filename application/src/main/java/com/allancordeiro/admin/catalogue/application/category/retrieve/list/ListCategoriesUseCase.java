package com.allancordeiro.admin.catalogue.application.category.retrieve.list;

import com.allancordeiro.admin.catalogue.application.UseCase;
import com.allancordeiro.admin.catalogue.domain.category.CategorySearchQuery;
import com.allancordeiro.admin.catalogue.domain.pagination.Pagination;

public abstract class ListCategoriesUseCase
        extends UseCase<CategorySearchQuery, Pagination<CategoryListOutput>> {
}
