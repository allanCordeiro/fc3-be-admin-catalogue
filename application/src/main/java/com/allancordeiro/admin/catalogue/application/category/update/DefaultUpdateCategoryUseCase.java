package com.allancordeiro.admin.catalogue.application.category.update;

import com.allancordeiro.admin.catalogue.domain.category.Category;
import com.allancordeiro.admin.catalogue.domain.category.CategoryGateway;
import com.allancordeiro.admin.catalogue.domain.category.CategoryId;
import com.allancordeiro.admin.catalogue.domain.exceptions.DomainException;
import com.allancordeiro.admin.catalogue.domain.exceptions.NotFoundException;
import com.allancordeiro.admin.catalogue.domain.validation.Error;
import com.allancordeiro.admin.catalogue.domain.validation.handler.Notification;
import io.vavr.API;
import io.vavr.control.Either;

import java.util.Objects;
import java.util.function.Supplier;

public class DefaultUpdateCategoryUseCase extends UpdateCategoryUseCase{
    private final CategoryGateway categoryGateway;

    public DefaultUpdateCategoryUseCase(final CategoryGateway categoryGateway) {
        this.categoryGateway = Objects.requireNonNull(categoryGateway);
    }

    @Override
    public Either<Notification, UpdateCategoryOutput> execute(final UpdateCategoryCommand command) {
        final var id = CategoryId.from(command.id());
        final var name = command.name();
        final var description = command.description();
        final var isActive = command.isActive();

        final var category = this.categoryGateway.findById(id)
                .orElseThrow(notFound(id));

        final var  notification = Notification.create();
        category
                .update(name, description, isActive)
                .validate(notification);

        return notification.hasError() ? API.Left(notification) : update(category);
    }

    private Either<Notification, UpdateCategoryOutput> update(final Category category) {
        return API.Try(() -> this.categoryGateway.update(category))
                .toEither()
                .bimap(Notification::create, UpdateCategoryOutput::from);
    }

    private static Supplier<DomainException> notFound(final CategoryId id) {
        return () -> NotFoundException.with(Category.class, id);
    }
}
