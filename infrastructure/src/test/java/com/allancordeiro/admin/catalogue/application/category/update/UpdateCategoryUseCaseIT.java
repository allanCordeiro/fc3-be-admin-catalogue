package com.allancordeiro.admin.catalogue.application.category.update;

import com.allancordeiro.admin.catalogue.IntegrationTest;
import com.allancordeiro.admin.catalogue.application.category.retrieve.get.CategoryOutput;
import com.allancordeiro.admin.catalogue.domain.category.Category;
import com.allancordeiro.admin.catalogue.domain.category.CategoryGateway;
import com.allancordeiro.admin.catalogue.domain.category.CategoryId;
import com.allancordeiro.admin.catalogue.domain.exceptions.DomainException;
import com.allancordeiro.admin.catalogue.domain.exceptions.NotFoundException;
import com.allancordeiro.admin.catalogue.infrastructure.category.persistence.CategoryJpaEntity;
import com.allancordeiro.admin.catalogue.infrastructure.category.persistence.CategoryRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.SpyBean;

import java.util.Arrays;
import java.util.Optional;

import static org.mockito.AdditionalAnswers.returnsFirstArg;

@IntegrationTest
public class UpdateCategoryUseCaseIT {
    @Autowired
    private UpdateCategoryUseCase useCase;
    @Autowired
    private CategoryRepository categoryRepository;
    @SpyBean
    private CategoryGateway categoryGateway;

    @Test
    public void givenAValidCommand_whenCallsUpdateCategory_thenShouldReturnCategoryId() {
        final var category = Category.newCategory("Film", null, true);
        final var expectedId = category.getId();
        final var expectedName = "Filmes";
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = true;
        save(category);

        final var command = UpdateCategoryCommand.with(
                expectedId.getValue(),
                expectedName,
                expectedDescription,
                expectedIsActive);

        Assertions.assertEquals(1, categoryRepository.count());
        final var actualOutput = useCase.execute(command).get();

        final var actualCategory = categoryRepository.findById(actualOutput.id()).get();

        Assertions.assertNotNull(actualCategory.getId());
        Assertions.assertEquals(expectedName, actualCategory.getName());
        Assertions.assertEquals(expectedDescription, actualCategory.getDescription());
        Assertions.assertEquals(expectedIsActive, actualCategory.isActive());
        Assertions.assertEquals(category.getCreatedAt(), actualCategory.getCreatedAt());
        Assertions.assertTrue(category.getUpdatedAt().isBefore(actualCategory.getUpdatedAt()));
        Assertions.assertEquals(category.getDeletedAt(), actualCategory.getDeletedAt());
    }

    @Test
    public void givenAnInvalidName_whenCallsUpdateCategory_thenShouldReturnDomainException() {
        final var category = Category.newCategory("Film", null, true);
        final String expectedName = null;
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = true;
        final var expectedId = category.getId();
        final var expectedErrorMessage = "'name' should not be null";
        final var expectedErrorCount = 1;

        save(category);

        final var command =
                UpdateCategoryCommand.with(expectedId.getValue(), expectedName, expectedDescription, expectedIsActive);


        final var notification = useCase.execute(command).getLeft();
        Assertions.assertEquals(expectedErrorMessage, notification.firstError().message());
        Assertions.assertEquals(expectedErrorCount, notification.getErrors().size());
        Mockito.verify(categoryGateway, Mockito.times(0)).update(Mockito.any());
    }

    @Test
    public void givenAValidInactiveCommand_whenCallsUpdateCategory_thenShouldReturnInactiveCategoryId() {
        final var category = Category.newCategory("Film", null, true);
        final var expectedId = category.getId();
        final var expectedName = "Filmes";
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = false;

        save(category);

        final var command = UpdateCategoryCommand.with(
                expectedId.getValue(),
                expectedName,
                expectedDescription,
                expectedIsActive);


        Assertions.assertTrue(category.isActive());
        Assertions.assertNull(category.getDeletedAt());

        final var actualOutput = useCase.execute(command).get();

        Assertions.assertNotNull(actualOutput);
        Assertions.assertNotNull(actualOutput.id());

        final var actualCategory = categoryRepository.findById(actualOutput.id()).get();

        Assertions.assertNotNull(actualCategory.getId());
        Assertions.assertEquals(expectedName, actualCategory.getName());
        Assertions.assertEquals(expectedDescription, actualCategory.getDescription());
        Assertions.assertEquals(expectedIsActive, actualCategory.isActive());
        Assertions.assertEquals(category.getCreatedAt(), actualCategory.getCreatedAt());
        Assertions.assertTrue(category.getUpdatedAt().isBefore(actualCategory.getUpdatedAt()));
        Assertions.assertNotNull(actualCategory.getDeletedAt());

    }

    @Test
    public void givenAValidCommand_whenGatewayThrowsRandomException_thenShouldReturnAnException() {
        final var category = Category.newCategory("Film", null, true);
        final var expectedName = "Filmes";
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = true;
        final var expectedId = category.getId();
        final var expectedErrorMessage = "Gateway error";

        save(category);

        final var command =
                UpdateCategoryCommand.with(expectedId.getValue(), expectedName, expectedDescription, expectedIsActive);


        Mockito.doThrow(new IllegalStateException(expectedErrorMessage))
                .when(categoryGateway).update(Mockito.any());

        final var notification = useCase.execute(command).getLeft();
        Assertions.assertEquals(expectedErrorMessage, notification.firstError().message());

        final var actualCategory = categoryRepository.findById(expectedId.getValue()).get();

        Assertions.assertNotNull(actualCategory.getId());
        Assertions.assertEquals(category.getName(), actualCategory.getName());
        Assertions.assertEquals(category.getDescription(), actualCategory.getDescription());
        Assertions.assertEquals(category.isActive(), actualCategory.isActive());
        Assertions.assertEquals(category.getCreatedAt(), actualCategory.getCreatedAt());
        Assertions.assertEquals(category.getUpdatedAt(), actualCategory.getUpdatedAt());
        Assertions.assertEquals(category.getDeletedAt(), actualCategory.getDeletedAt());
    }

    @Test
    public void givenACommandWithInvalidID_whenCallsUpdateCategory_thenShouldReturnNotFoundException() {

        final var expectedName = "Filmes";
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = false;
        final var expectedId = "123";
        final var expectErrorMessage = "Category with ID %s was not found".formatted(expectedId);

        final var command = UpdateCategoryCommand.with(
                expectedId,
                expectedName,
                expectedDescription,
                expectedIsActive);


        final var actualException =
                Assertions.assertThrows(NotFoundException.class, () -> useCase.execute(command));

        Assertions.assertEquals(expectErrorMessage, actualException.getMessage());

    }

    private void save(final Category... category) {
        categoryRepository.saveAllAndFlush(
                Arrays.stream(category)
                        .map(CategoryJpaEntity::from)
                        .toList()
        );
    }
}
