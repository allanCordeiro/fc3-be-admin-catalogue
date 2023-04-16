package com.allancordeiro.admin.catalogue.application.category.create;

import com.allancordeiro.admin.catalogue.application.UseCase;
import com.allancordeiro.admin.catalogue.domain.validation.handler.Notification;
import io.vavr.control.Either;

public abstract class CreateCategoryUseCase extends UseCase<CreateCategoryCommand, Either<Notification, CreateCategoryOutput>> {

}
