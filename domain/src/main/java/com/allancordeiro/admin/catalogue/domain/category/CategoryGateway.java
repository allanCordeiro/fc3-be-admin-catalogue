package com.allancordeiro.admin.catalogue.domain.category;

import com.allancordeiro.admin.catalogue.domain.pagination.Pagination;

import java.util.Optional;

public interface CategoryGateway {
    Category create(Category category);
    void deleteById(CategoryId id);
    Optional<Category> findById(CategoryId id);
    Category update(Category category);
    Pagination<Category> findAll(CategorySearchQuery query);
}
