package com.allancordeiro.admin.catalogue.application.category.update;

import com.allancordeiro.admin.catalogue.application.category.create.CreateCategoryCommand;
import com.allancordeiro.admin.catalogue.application.category.create.DefaultCreateCategoryUseCase;
import com.allancordeiro.admin.catalogue.domain.category.Category;
import com.allancordeiro.admin.catalogue.domain.category.CategoryGateway;
import com.allancordeiro.admin.catalogue.domain.category.CategoryId;
import com.allancordeiro.admin.catalogue.domain.exceptions.DomainException;
import com.allancordeiro.admin.catalogue.domain.exceptions.NotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Optional;

import static org.mockito.AdditionalAnswers.returnsFirstArg;

@ExtendWith(MockitoExtension.class)
public class UpdateCategoryUseCaseTest {
    @InjectMocks
    private DefaultUpdateCategoryUseCase useCase;
    @Mock
    private CategoryGateway categoryGateway;

    @BeforeEach
    void cleanUp() {
        Mockito.reset(categoryGateway);
    }

    @Test
    public void givenAValidCommand_whenCallsUpdateCategory_thenShouldReturnCategoryId() {
        final var category = Category.newCategory("Film", null, true);
        final var expectedName = "Filmes";
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = true;

        final var expectedId = category.getId();

        final var command = UpdateCategoryCommand.with(
                expectedId.getValue(),
                expectedName,
                expectedDescription,
                expectedIsActive);

        Mockito.when(categoryGateway.findById(Mockito.eq(expectedId)))
                .thenReturn(Optional.of(category.clone()));

        Mockito.when(categoryGateway.update(Mockito.any()))
                .thenAnswer(returnsFirstArg());

        final var actualOutput = useCase.execute(command).get();

        Assertions.assertNotNull(actualOutput);
        Assertions.assertNotNull(actualOutput.id());

        Mockito.verify(categoryGateway, Mockito.times(1)).findById(Mockito.eq(expectedId));

        final var captor = ArgumentCaptor.forClass(Category.class);
        Mockito.verify(categoryGateway, Mockito.times(1)).update(captor.capture());
        final var updatedCategory = captor.getValue();

        Assertions.assertEquals(expectedName, updatedCategory.getName());
        Assertions.assertEquals(expectedDescription, updatedCategory.getDescription());
        Assertions.assertEquals(expectedIsActive, updatedCategory.isActive());
        Assertions.assertEquals(expectedId, updatedCategory.getId());
        Assertions.assertEquals(category.getCreatedAt(), updatedCategory.getCreatedAt());
        Assertions.assertTrue(category.getUpdatedAt().isBefore(updatedCategory.getUpdatedAt()));
        Assertions.assertNotNull(updatedCategory.getUpdatedAt());
        Assertions.assertNull(updatedCategory.getDeletedAt());
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

        final var command =
                UpdateCategoryCommand.with(expectedId.getValue(), expectedName, expectedDescription, expectedIsActive);

        Mockito.when(categoryGateway.findById(Mockito.eq(expectedId)))
                .thenReturn(Optional.of(category.clone()));

        final var notification = useCase.execute(command).getLeft();
        Assertions.assertEquals(expectedErrorMessage, notification.firstError().message());
        Assertions.assertEquals(expectedErrorCount, notification.getErrors().size());
        Mockito.verify(categoryGateway, Mockito.times(0)).update(Mockito.any());
    }

    @Test
    public void givenAValidInactiveCommand_whenCallsUpdateCategory_thenShouldReturnInactiveCategoryId() {
        final var category = Category.newCategory("Film", null, true);
        final var expectedName = "Filmes";
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = false;

        final var expectedId = category.getId();

        final var command = UpdateCategoryCommand.with(
                expectedId.getValue(),
                expectedName,
                expectedDescription,
                expectedIsActive);

        Mockito.when(categoryGateway.findById(Mockito.eq(expectedId)))
                .thenReturn(Optional.of(category.clone()));

        Mockito.when(categoryGateway.update(Mockito.any()))
                .thenAnswer(returnsFirstArg());

        Assertions.assertTrue(category.isActive());
        Assertions.assertNull(category.getDeletedAt());

        final var actualOutput = useCase.execute(command).get();

        Assertions.assertNotNull(actualOutput);
        Assertions.assertNotNull(actualOutput.id());

        Mockito.verify(categoryGateway, Mockito.times(1)).findById(Mockito.eq(expectedId));

        final var captor = ArgumentCaptor.forClass(Category.class);
        Mockito.verify(categoryGateway, Mockito.times(1)).update(captor.capture());
        final var updatedCategory = captor.getValue();

        Assertions.assertEquals(expectedName, updatedCategory.getName());
        Assertions.assertEquals(expectedDescription, updatedCategory.getDescription());
        Assertions.assertEquals(expectedIsActive, updatedCategory.isActive());
        Assertions.assertEquals(expectedId, updatedCategory.getId());
        Assertions.assertEquals(category.getCreatedAt(), updatedCategory.getCreatedAt());
        Assertions.assertTrue(category.getUpdatedAt().isBefore(updatedCategory.getUpdatedAt()));
        Assertions.assertNotNull(updatedCategory.getUpdatedAt());
        Assertions.assertNotNull(updatedCategory.getDeletedAt());
    }

    @Test
    public void givenAValidCommand_whenGatewayThrowsRandomException_thenShouldReturnAnException() {
        final var category = Category.newCategory("Film", null, true);
        final var expectedName = "Filmes";
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = true;
        final var expectedId = category.getId();
        final var expectedErrorMessage = "Gateway error";

        final var command =
                UpdateCategoryCommand.with(expectedId.getValue(), expectedName, expectedDescription, expectedIsActive);

        Mockito.when(categoryGateway.findById(Mockito.eq(expectedId)))
                .thenReturn(Optional.of(category.clone()));

        Mockito.when(categoryGateway.update(Mockito.any()))
                .thenThrow(new IllegalStateException(expectedErrorMessage));

        final var notification = useCase.execute(command).getLeft();
        Assertions.assertEquals(expectedErrorMessage, notification.firstError().message());

        final var captor = ArgumentCaptor.forClass(Category.class);
        Mockito.verify(categoryGateway, Mockito.times(1)).update(captor.capture());
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

        Mockito.when(categoryGateway.findById(Mockito.eq(CategoryId.from(expectedId))))
                .thenReturn(Optional.empty());


        final var actualException =
                Assertions.assertThrows(NotFoundException.class, () -> useCase.execute(command));

        Assertions.assertEquals(expectErrorMessage, actualException.getMessage());

        Mockito.verify(categoryGateway, Mockito.times(1))
                .findById(Mockito.eq(CategoryId.from(expectedId)));

        Mockito.verify(categoryGateway, Mockito.times(0)).update(Mockito.any());
    }
}
