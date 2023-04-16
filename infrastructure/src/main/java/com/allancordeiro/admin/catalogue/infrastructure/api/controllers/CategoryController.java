package com.allancordeiro.admin.catalogue.infrastructure.api.controllers;

import com.allancordeiro.admin.catalogue.application.category.create.CreateCategoryCommand;
import com.allancordeiro.admin.catalogue.application.category.create.CreateCategoryOutput;
import com.allancordeiro.admin.catalogue.application.category.create.CreateCategoryUseCase;
import com.allancordeiro.admin.catalogue.application.category.delete.DeleteCategoryUseCase;
import com.allancordeiro.admin.catalogue.application.category.retrieve.get.GetCategoryByIdUseCase;
import com.allancordeiro.admin.catalogue.application.category.retrieve.list.ListCategoriesUseCase;
import com.allancordeiro.admin.catalogue.application.category.update.UpdateCategoryCommand;
import com.allancordeiro.admin.catalogue.application.category.update.UpdateCategoryOutput;
import com.allancordeiro.admin.catalogue.application.category.update.UpdateCategoryUseCase;
import com.allancordeiro.admin.catalogue.domain.category.CategorySearchQuery;
import com.allancordeiro.admin.catalogue.domain.pagination.Pagination;
import com.allancordeiro.admin.catalogue.domain.validation.handler.Notification;
import com.allancordeiro.admin.catalogue.infrastructure.api.CategoryAPI;
import com.allancordeiro.admin.catalogue.infrastructure.category.models.CategoryListResponse;
import com.allancordeiro.admin.catalogue.infrastructure.category.models.CategoryResponse;
import com.allancordeiro.admin.catalogue.infrastructure.category.models.CreateCategoryRequest;
import com.allancordeiro.admin.catalogue.infrastructure.category.models.UpdateCategoryRequest;
import com.allancordeiro.admin.catalogue.infrastructure.category.presenters.CategoryApiPresenter;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.Objects;
import java.util.function.Function;

@RestController
public class CategoryController implements CategoryAPI {

    private final CreateCategoryUseCase createCategoryUseCase;
    private final GetCategoryByIdUseCase getCategoryByIdUseCase;
    private final UpdateCategoryUseCase updateCategoryUseCase;
    private final DeleteCategoryUseCase deleteCategoryUseCase;
    private final ListCategoriesUseCase listCategoriesUseCase;

    public CategoryController(
            final CreateCategoryUseCase createCategoryUseCase,
            final GetCategoryByIdUseCase getCategoryByIdUseCase,
            final UpdateCategoryUseCase updateCategoryUseCase,
            final DeleteCategoryUseCase deleteCategoryUseCase,
            final ListCategoriesUseCase listCategoriesUseCase
    ) {
        this.createCategoryUseCase = Objects.requireNonNull(createCategoryUseCase);
        this.getCategoryByIdUseCase = Objects.requireNonNull(getCategoryByIdUseCase);
        this.updateCategoryUseCase = Objects.requireNonNull(updateCategoryUseCase);
        this.deleteCategoryUseCase = Objects.requireNonNull(deleteCategoryUseCase);
        this.listCategoriesUseCase = Objects.requireNonNull(listCategoriesUseCase);
    }

    @Override
    public ResponseEntity<?> createCategory(final CreateCategoryRequest input) {
        final var command = CreateCategoryCommand.with(
                input.name(),
                input.description(),
                input.active() != null ? input.active() : true
        );

        final Function<Notification, ResponseEntity<?>> onError = ResponseEntity.unprocessableEntity()::body;
        final Function<CreateCategoryOutput, ResponseEntity<?>> onSuccess = output ->
                ResponseEntity.created(URI.create("/categories/" + output.id())).body(output);

        return this.createCategoryUseCase.execute(command)
                .fold(onError, onSuccess);
    }

    @Override
    public Pagination<CategoryListResponse> listCategories(
            final String search,
            final int page,
            final int perPage,
            final String sort,
            final String direction) {
        return listCategoriesUseCase
                .execute(new CategorySearchQuery(page, perPage, search, sort, direction))
                .map(CategoryApiPresenter::present);
    }

    @Override
    public CategoryResponse getById(String id) {
        return CategoryApiPresenter.present(this.getCategoryByIdUseCase.execute(id));
    }

    @Override
    public ResponseEntity<?> updateById(final String id, final UpdateCategoryRequest input) {
        final var command = UpdateCategoryCommand.with(
                id,
                input.name(),
                input.description(),
                input.active() != null ? input.active() : true
        );

        final Function<Notification, ResponseEntity<?>> onError = ResponseEntity.unprocessableEntity()::body;
        final Function<UpdateCategoryOutput, ResponseEntity<?>> onSuccess = ResponseEntity::ok;

        return this.updateCategoryUseCase.execute(command)
                .fold(onError, onSuccess);
    }

    @Override
    public void deleteById(final String id) {
        this.deleteCategoryUseCase.execute(id);
    }
}
