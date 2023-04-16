package com.allancordeiro.admin.catalogue.infrastructure.category;

import com.allancordeiro.admin.catalogue.domain.category.Category;
import com.allancordeiro.admin.catalogue.domain.category.CategoryGateway;
import com.allancordeiro.admin.catalogue.domain.category.CategoryId;
import com.allancordeiro.admin.catalogue.domain.category.CategorySearchQuery;
import com.allancordeiro.admin.catalogue.domain.pagination.Pagination;
import com.allancordeiro.admin.catalogue.infrastructure.category.persistence.CategoryJpaEntity;
import com.allancordeiro.admin.catalogue.infrastructure.category.persistence.CategoryRepository;
import com.allancordeiro.admin.catalogue.infrastructure.utils.SpecificationUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.*;
import java.util.Objects;
import java.util.Optional;

@Service
public class CategoryMySQLGateway implements CategoryGateway {
    private final CategoryRepository repository;

    public CategoryMySQLGateway(final CategoryRepository repository) {
        this.repository = repository;
    }

    @Override
    public Category create(final Category category) {
        return this.save(category);
    }

    @Override
    public void deleteById(final CategoryId id) {
        String idValue = id.getValue();
        if(this.repository.existsById(idValue)) {
            this.repository.deleteById(idValue);
        }
    }

    @Override
    public Optional<Category> findById(final CategoryId id) {
        return this.repository.findById(id.getValue())
                .map(CategoryJpaEntity::toAggregate);
    }

    @Override
    public Category update(final Category category) {
        return this.save(category);
    }

    @Override
    public Pagination<Category> findAll(final CategorySearchQuery query) {
        final var page = PageRequest.of(
                query.page(),
                query.perPage(),
                Sort.by(Sort.Direction.fromString(query.direction()), query.sort())
        );
        final var specifications = Optional.ofNullable(query.terms())
                .filter(str -> !str.isBlank())
                .map(str -> SpecificationUtils
                        .<CategoryJpaEntity>like("name", str)
                        .or(SpecificationUtils.like("description", str))
                )
                .orElse(null);

        final var pageResult = this.repository.findAll(Specification.where(specifications), page);

        return new Pagination<>(
                pageResult.getNumber(),
                pageResult.getSize(),
                pageResult.getTotalElements(),
                pageResult.map(CategoryJpaEntity::toAggregate).toList()
        );
    }

    private Category save(final Category category) {
        return this.repository.save(CategoryJpaEntity.from(category)).toAggregate();
    }
}
