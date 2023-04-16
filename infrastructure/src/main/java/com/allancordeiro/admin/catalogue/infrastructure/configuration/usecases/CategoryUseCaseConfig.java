package com.allancordeiro.admin.catalogue.infrastructure.configuration.usecases;

import com.allancordeiro.admin.catalogue.application.category.create.CreateCategoryUseCase;
import com.allancordeiro.admin.catalogue.application.category.create.DefaultCreateCategoryUseCase;
import com.allancordeiro.admin.catalogue.application.category.delete.DefaultDeleteCategoryUseCase;
import com.allancordeiro.admin.catalogue.application.category.delete.DeleteCategoryUseCase;
import com.allancordeiro.admin.catalogue.application.category.retrieve.get.DefaultGetCategoryByIdUseCase;
import com.allancordeiro.admin.catalogue.application.category.retrieve.get.GetCategoryByIdUseCase;
import com.allancordeiro.admin.catalogue.application.category.retrieve.list.DefaultListCategoriesUseCase;
import com.allancordeiro.admin.catalogue.application.category.retrieve.list.ListCategoriesUseCase;
import com.allancordeiro.admin.catalogue.application.category.update.DefaultUpdateCategoryUseCase;
import com.allancordeiro.admin.catalogue.application.category.update.UpdateCategoryUseCase;
import com.allancordeiro.admin.catalogue.domain.category.CategoryGateway;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CategoryUseCaseConfig {
    private final CategoryGateway categoryGateway;

    public CategoryUseCaseConfig(final CategoryGateway categoryGateway) {
        this.categoryGateway = categoryGateway;
    }

    @Bean
    public CreateCategoryUseCase createCategoryUseCase() {
        return new DefaultCreateCategoryUseCase(categoryGateway);
    }
    @Bean
    public UpdateCategoryUseCase updateCategoryUseCaseCategoryUseCase() {
        return new DefaultUpdateCategoryUseCase(categoryGateway);
    }

    @Bean
    public GetCategoryByIdUseCase getCategoryByIdUseCase() {
        return new DefaultGetCategoryByIdUseCase(categoryGateway);
    }

    @Bean
    public ListCategoriesUseCase listCategoriesUseCase() {
        return new DefaultListCategoriesUseCase(categoryGateway);
    }

    @Bean
    public DeleteCategoryUseCase deleteCategoryUseCaseCategoryUseCase() {
        return new DefaultDeleteCategoryUseCase(categoryGateway);
    }
}
