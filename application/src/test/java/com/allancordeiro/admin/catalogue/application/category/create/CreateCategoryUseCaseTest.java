package com.allancordeiro.admin.catalogue.application.category.create;

import com.allancordeiro.admin.catalogue.domain.category.Category;
import com.allancordeiro.admin.catalogue.domain.category.CategoryGateway;
import com.allancordeiro.admin.catalogue.domain.exceptions.DomainException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Objects;

import static org.mockito.AdditionalAnswers.returnsFirstArg;

@ExtendWith(MockitoExtension.class)
public class CreateCategoryUseCaseTest {
    @InjectMocks
    private DefaultCreateCategoryUseCase useCase;
    @Mock
    private CategoryGateway categoryGateway;

    @BeforeEach
    void cleanUp() {
        Mockito.reset(categoryGateway);
    }

    @Test
    public void givenAValidCommand_whenCallsCreateCategory_thenShouldReturnCategoryId() {
        final var expectedName = "Filmes";
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = true;

        final var command = CreateCategoryCommand.with(expectedName, expectedDescription, expectedIsActive);

        Mockito.when(categoryGateway.create(Mockito.any()))
                .thenAnswer(returnsFirstArg());
        final var actualOutput =  useCase.execute(command).get();

        Assertions.assertNotNull(actualOutput);
        Assertions.assertNotNull(actualOutput.id());

        final var captor = ArgumentCaptor.forClass(Category.class);
        Mockito.verify(categoryGateway, Mockito.times(1)).create(captor.capture());
        final var insertedCategory = captor.getValue();

        Assertions.assertEquals(expectedName, insertedCategory.getName());
        Assertions.assertEquals(expectedDescription, insertedCategory.getDescription());
        Assertions.assertEquals(expectedIsActive, insertedCategory.isActive());
        Assertions.assertNotNull(insertedCategory.getId());
        Assertions.assertNotNull(insertedCategory.getCreatedAt());
        Assertions.assertNotNull(insertedCategory.getUpdatedAt());
        Assertions.assertNull(insertedCategory.getDeletedAt());
    }

    @Test
    public void givenAnInvalidName_whenCallsCreateCategory_thenShouldReturnDomainException() {
        final String expectedName = null;
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = true;
        final var expectedErrorMessage = "'name' should not be null";
        final var expectedErrorCount = 1;

        final var command = CreateCategoryCommand.with(expectedName, expectedDescription, expectedIsActive);
        final CategoryGateway categoryGateway = Mockito.mock(CategoryGateway.class);
        final var useCase = new DefaultCreateCategoryUseCase(categoryGateway);

        final var notification = useCase.execute(command).getLeft();
        Assertions.assertEquals(expectedErrorMessage, notification.firstError().message());
        Assertions.assertEquals(expectedErrorCount, notification.getErrors().size());
        Mockito.verify(categoryGateway, Mockito.times(0)).create(Mockito.any());
    }

    @Test
    public void givenAValidCommandWithInactiveCategory_whenCallsCreateCategory_thenShouldReturnInactiveCategoryId() {
        final var expectedName = "Filmes";
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = false;

        final var command = CreateCategoryCommand.with(expectedName, expectedDescription, expectedIsActive);

        Mockito.when(categoryGateway.create(Mockito.any()))
                .thenAnswer(returnsFirstArg());
        final var actualOutput =  useCase.execute(command).get();

        Assertions.assertNotNull(actualOutput);
        Assertions.assertNotNull(actualOutput.id());

        final var captor = ArgumentCaptor.forClass(Category.class);
        Mockito.verify(categoryGateway, Mockito.times(1)).create(captor.capture());
        final var insertedCategory = captor.getValue();

        Assertions.assertEquals(expectedName, insertedCategory.getName());
        Assertions.assertEquals(expectedDescription, insertedCategory.getDescription());
        Assertions.assertEquals(expectedIsActive, insertedCategory.isActive());
        Assertions.assertNotNull(insertedCategory.getId());
        Assertions.assertNotNull(insertedCategory.getCreatedAt());
        Assertions.assertNotNull(insertedCategory.getUpdatedAt());
        Assertions.assertNotNull(insertedCategory.getDeletedAt());
    }

    @Test
    public void givenAValidCommand_whenGatewayThrowsRandomException_thenShouldReturnAnException() {
        final var expectedName = "Filmes";
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = true;
        final var expectedErrorMessage = "Gateway error";

        final var command = CreateCategoryCommand.with(expectedName, expectedDescription, expectedIsActive);

        Mockito.when(categoryGateway.create(Mockito.any()))
                .thenThrow(new IllegalStateException(expectedErrorMessage));

        final var notification = useCase.execute(command).getLeft();
        Assertions.assertEquals(expectedErrorMessage, notification.firstError().message());

        final var captor = ArgumentCaptor.forClass(Category.class);
        Mockito.verify(categoryGateway, Mockito.times(1)).create(captor.capture());
        final var insertedCategory = captor.getValue();

        Assertions.assertEquals(expectedName, insertedCategory.getName());
        Assertions.assertEquals(expectedDescription, insertedCategory.getDescription());
        Assertions.assertEquals(expectedIsActive, insertedCategory.isActive());
        Assertions.assertNotNull(insertedCategory.getId());
        Assertions.assertNotNull(insertedCategory.getCreatedAt());
        Assertions.assertNotNull(insertedCategory.getUpdatedAt());
        Assertions.assertNull(insertedCategory.getDeletedAt());
    }
}
