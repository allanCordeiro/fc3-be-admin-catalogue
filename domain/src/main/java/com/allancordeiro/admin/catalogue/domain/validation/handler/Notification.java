package com.allancordeiro.admin.catalogue.domain.validation.handler;

import com.allancordeiro.admin.catalogue.domain.exceptions.DomainException;
import com.allancordeiro.admin.catalogue.domain.validation.Error;
import com.allancordeiro.admin.catalogue.domain.validation.ValidationHandler;

import java.util.ArrayList;
import java.util.List;

public class Notification implements ValidationHandler {
    private final List<Error> errors;

    private Notification(List<Error> errors) {
        this.errors = errors;
    }

    public static Notification create() {
        return new Notification(new ArrayList<>());
    }

    public static Notification create(final Throwable t) {
        return create(new Error((t.getMessage())));
    }
    public static Notification create(final Error error) {
        return new Notification(new ArrayList<>()).append(error);
    }

    @Override
    public Notification append(final Error error) {
        this.errors.add(error);
        return this;
    }

    @Override
    public Notification append(final ValidationHandler handler) {
        this.errors.addAll(handler.getErrors());
        return this;
    }

    @Override
    public Notification validate(final Validation validation) {
        try {
            validation.validate();
        } catch (final DomainException ex) {
            this.errors.addAll(ex.getErrors());
        } catch (final Throwable t) {
            this.errors.add(new Error(t.getMessage()));
        }
        return null;
    }

    @Override
    public List<Error> getErrors() {
        return this.errors;
    }
}
