package com.allancordeiro.admin.catalogue.application.category.retrieve.get;

import com.allancordeiro.admin.catalogue.IntegrationTest;
import com.allancordeiro.admin.catalogue.domain.category.Category;
import com.allancordeiro.admin.catalogue.domain.category.CategoryGateway;
import com.allancordeiro.admin.catalogue.domain.category.CategoryId;
import com.allancordeiro.admin.catalogue.domain.exceptions.DomainException;
import com.allancordeiro.admin.catalogue.domain.exceptions.NotFoundException;
import com.allancordeiro.admin.catalogue.infrastructure.category.persistence.CategoryJpaEntity;
import com.allancordeiro.admin.catalogue.infrastructure.category.persistence.CategoryRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.SpyBean;

import java.util.Arrays;
import java.util.Optional;

@IntegrationTest
public class GetCategoryByIdUseCaseIT {
    @Autowired
    private GetCategoryByIdUseCase useCase;
    @Autowired
    private CategoryRepository categoryRepository;
    @SpyBean
    private CategoryGateway categoryGateway;

    @Test
    public void givenAValidId_whenCallsGetCategory_shouldReturnCategory() {
        final var expectedName = "Filmes";
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = true;
        final var category = Category.newCategory(expectedName,expectedDescription, expectedIsActive);
        final var expectedId = category.getId();

        save(category);

        final var actualCategory = useCase.execute(expectedId.getValue());

        Assertions.assertEquals(CategoryOutput.from(category), actualCategory);
        Assertions.assertEquals(expectedId, actualCategory.id());
        Assertions.assertEquals(expectedName, actualCategory.name());
        Assertions.assertEquals(expectedDescription, actualCategory.description());
        Assertions.assertEquals(expectedIsActive, actualCategory.isActive());
        Assertions.assertEquals(category.getCreatedAt(), actualCategory.createdAt());
        Assertions.assertEquals(category.getUpdatedAt(), actualCategory.updatedAt());
        Assertions.assertEquals(category.getDeletedAt(), actualCategory.deletedAt());
    }

    @Test
    public void givenAnIValidId_whenCallsGetCategory_shouldReturnNotFound() {
        final var expectedId = CategoryId.from("123");
        final var expectedErrorMessage = "Category with ID %s was not found".formatted(expectedId.getValue());
        final var expectedErrorCount = 1;


        final var actualException = Assertions.assertThrows(
                NotFoundException.class,
                () -> useCase.execute(expectedId.getValue())
        );

        //Assertions.assertEquals(expectedErrorCount, actualException.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, actualException.getMessage());
    }

    @Test
    public void givenAValidId_whenGatewayThrowsException_shouldReturnException() {
        final var expectedId = CategoryId.from("123");
        final var expectedErrorMessage = "Gateway error";

        Mockito.doThrow(new IllegalStateException(expectedErrorMessage))
                .when(categoryGateway).findById(expectedId);

        final var actualException = Assertions.assertThrows(
                IllegalStateException.class,
                () -> useCase.execute(expectedId.getValue())
        );

        Assertions.assertEquals(expectedErrorMessage, actualException.getMessage());
    }

    private void save(final Category... category) {
        categoryRepository.saveAllAndFlush(
                Arrays.stream(category)
                        .map(CategoryJpaEntity::from)
                        .toList()
        );
    }
}
