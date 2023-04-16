package com.allancordeiro.admin.catalogue.application.category.update;

import com.allancordeiro.admin.catalogue.application.UseCase;
import com.allancordeiro.admin.catalogue.domain.validation.handler.Notification;
import io.vavr.control.Either;

public abstract  class UpdateCategoryUseCase
        extends UseCase<UpdateCategoryCommand, Either<Notification, UpdateCategoryOutput>> {
}
