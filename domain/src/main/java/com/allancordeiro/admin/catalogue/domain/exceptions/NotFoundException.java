package com.allancordeiro.admin.catalogue.domain.exceptions;

import com.allancordeiro.admin.catalogue.domain.AggregateRoot;
import com.allancordeiro.admin.catalogue.domain.Identifier;
import com.allancordeiro.admin.catalogue.domain.validation.Error;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class NotFoundException extends DomainException {
    protected NotFoundException(final String message, final List<Error> errors) {
        super(message, errors);
    }

    public static NotFoundException with(final Class<? extends AggregateRoot<?>> aggregate, final Identifier id) {
        final var error = "%s with ID %s was not found".formatted(aggregate.getSimpleName(), id.getValue());

        return new NotFoundException(error, Collections.emptyList());
    }
}
