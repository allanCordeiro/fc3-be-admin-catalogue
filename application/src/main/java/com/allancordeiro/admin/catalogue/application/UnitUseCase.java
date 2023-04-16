package com.allancordeiro.admin.catalogue.application;

public abstract class UnitUseCase<IN> {
    public abstract void execute(IN in);
}
