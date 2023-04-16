package com.allancordeiro.admin.catalogue.application;

import com.allancordeiro.admin.catalogue.domain.category.Category;

public abstract  class UseCase<IN, OUT> {
    public abstract OUT execute(IN in);
}