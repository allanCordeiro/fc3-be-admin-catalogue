package com.allancordeiro.admin.catalogue.domain.category;

import com.allancordeiro.admin.catalogue.domain.exceptions.DomainException;
import com.allancordeiro.admin.catalogue.domain.validation.handler.ThrowsValidationHandler;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CategoryTest {
    @Test
    public void givenParams_whenCallNewCategory_thenInstantiateANewCategory() {
        final var expectedName = "nome de categoria";
        final var expectedDescription = "descricao de categoria";
        final var isActive = true;

        final var actualCategory = Category.newCategory(expectedName, expectedDescription, isActive);

        Assertions.assertNotNull(actualCategory);
        Assertions.assertNotNull(actualCategory.getId());
        Assertions.assertEquals(expectedName, actualCategory.getName());
        Assertions.assertEquals(expectedDescription, actualCategory.getDescription());
        Assertions.assertEquals(isActive, actualCategory.isActive());
        Assertions.assertNotNull(actualCategory.getCreatedAt());
        Assertions.assertNotNull(actualCategory.getUpdatedAt());
        Assertions.assertNull(actualCategory.getDeletedAt());
    }

    @Test
    public void givenNullName_whenCallNewCategoryAndValidate_thenShouldReturnAnError() {
        final String expectedName = null;
        final var expectedDescription = "descricao de categoria";
        final var isActive = true;
        final var expectedErrorMessage = "'name' should not be null";
        final var exectedErrorCount = 1;

        final var actualCategory = Category.newCategory(expectedName, expectedDescription, isActive);

        final var actualException = Assertions.assertThrows(
                DomainException.class, () -> actualCategory.validate(new ThrowsValidationHandler())
        );

        Assertions.assertEquals(expectedErrorMessage, actualException.getErrors().get(0).message());
        Assertions.assertEquals(exectedErrorCount, actualException.getErrors().size());
    }

    @Test
    public void givenEmptyName_whenCallNewCategoryAndValidate_thenShouldReturnAnError() {
        final String expectedName = " ";
        final var expectedDescription = "descricao de categoria";
        final var isActive = true;
        final var expectedErrorMessage = "'name' should not be empty";
        final var exectedErrorCount = 1;

        final var actualCategory = Category.newCategory(expectedName, expectedDescription, isActive);

        final var actualException = Assertions.assertThrows(
                DomainException.class, () -> actualCategory.validate(new ThrowsValidationHandler())
        );

        Assertions.assertEquals(expectedErrorMessage, actualException.getErrors().get(0).message());
        Assertions.assertEquals(exectedErrorCount, actualException.getErrors().size());
    }

    @Test
    public void givenInvalidNameLengthLessThan3_whenCallNewCategoryAndValidate_thenShouldReturnAnError() {
        final String expectedName = "Fi ";
        final var expectedDescription = "descricao de categoria";
        final var isActive = true;
        final var expectedErrorMessage = "'name' must be between 3 and 255 characters";
        final var exectedErrorCount = 1;

        final var actualCategory = Category.newCategory(expectedName, expectedDescription, isActive);

        final var actualException = Assertions.assertThrows(
                DomainException.class, () -> actualCategory.validate(new ThrowsValidationHandler())
        );

        Assertions.assertEquals(expectedErrorMessage, actualException.getErrors().get(0).message());
        Assertions.assertEquals(exectedErrorCount, actualException.getErrors().size());
    }

    @Test
    public void givenInvalidNameGreaterThan255_whenCallNewCategoryAndValidate_thenShouldReturnAnError() {
        final var expectedName = """
            Caros amigos, o novo modelo estrutural aqui preconizado nos obriga à análise dos paradigmas corporativos. 
            A prática cotidiana prova que o surgimento do comércio virtual agrega valor ao estabelecimento das posturas 
            dos órgãos prática cotidiana prova que o surgimento do comércio virtual agrega valor ao estabelecimento
            """;
        System.out.println(expectedName.length());
        final var expectedDescription = "descricao de categoria";
        final var isActive = true;
        final var expectedErrorMessage = "'name' must be between 3 and 255 characters";
        final var exectedErrorCount = 1;

        final var actualCategory = Category.newCategory(expectedName, expectedDescription, isActive);

        final var actualException = Assertions.assertThrows(
                DomainException.class, () -> actualCategory.validate(new ThrowsValidationHandler())
        );

        Assertions.assertEquals(expectedErrorMessage, actualException.getErrors().get(0).message());
        Assertions.assertEquals(exectedErrorCount, actualException.getErrors().size());
    }

    @Test
    public void givenAValidEmptyDescription_whenCallNewCategoryAndValidate_thenShouldReturnSuccess() {
        final String expectedName = "Filmes";
        final var expectedDescription = "";
        final var isActive = true;

        final var actualCategory = Category.newCategory(expectedName, expectedDescription, isActive);

        Assertions.assertDoesNotThrow(() -> actualCategory.validate(new ThrowsValidationHandler()));
        Assertions.assertNotNull(actualCategory);
        Assertions.assertNotNull(actualCategory.getId());
        Assertions.assertEquals(expectedName, actualCategory.getName());
        Assertions.assertEquals(expectedDescription, actualCategory.getDescription());
        Assertions.assertEquals(isActive, actualCategory.isActive());
        Assertions.assertNotNull(actualCategory.getCreatedAt());
        Assertions.assertNotNull(actualCategory.getUpdatedAt());
        Assertions.assertNull(actualCategory.getDeletedAt());
    }

    @Test
    public void givenAValidIsActiveEqualsFalse_whenCallNewCategoryAndValidate_thenShouldReturnSuccess() {
        final String expectedName = "Filmes";
        final var expectedDescription = "a categoria mais assistida";
        final var isActive = false;

        final var actualCategory = Category.newCategory(expectedName, expectedDescription, isActive);

        Assertions.assertDoesNotThrow(() -> actualCategory.validate(new ThrowsValidationHandler()));
        Assertions.assertNotNull(actualCategory);
        Assertions.assertNotNull(actualCategory.getId());
        Assertions.assertEquals(expectedName, actualCategory.getName());
        Assertions.assertEquals(expectedDescription, actualCategory.getDescription());
        Assertions.assertEquals(isActive, actualCategory.isActive());
        Assertions.assertNotNull(actualCategory.getCreatedAt());
        Assertions.assertNotNull(actualCategory.getUpdatedAt());
        Assertions.assertNotNull(actualCategory.getDeletedAt());
    }

    @Test
    public void givenAValidActiveCategory_whenCallDeactive_thenReturnCategoryInactivated() {
        final String expectedName = "Filmes";
        final var expectedDescription = "a categoria mais assistida";
        final var expectedIsActive = false;

        final var category = Category.newCategory(expectedName, expectedDescription, true);
        Assertions.assertDoesNotThrow(() -> category.validate(new ThrowsValidationHandler()));

        final var updatedAt = category.getUpdatedAt();
        final var createdAt = category.getCreatedAt();

        Assertions.assertTrue(category.isActive());
        Assertions.assertNull(category.getDeletedAt());

        final var actualCategory = category.deactivate();

        Assertions.assertDoesNotThrow(() -> actualCategory.validate(new ThrowsValidationHandler()));
        Assertions.assertEquals(category.getId(), actualCategory.getId());
        Assertions.assertEquals(expectedName, actualCategory.getName());
        Assertions.assertEquals(expectedDescription, actualCategory.getDescription());
        Assertions.assertEquals(expectedIsActive, actualCategory.isActive());
        Assertions.assertEquals(createdAt, actualCategory.getCreatedAt());
        Assertions.assertTrue(actualCategory.getUpdatedAt().isAfter(updatedAt));
        Assertions.assertNotNull(actualCategory.getDeletedAt());
    }

    @Test
    public void givenAValidDeactivatedCategory_whenCallActived_thenReturnCategoryActivated() {
        final String expectedName = "Filmes";
        final var expectedDescription = "a categoria mais assistida";
        final var expectedIsActive = true;

        final var category = Category.newCategory(expectedName, expectedDescription, false);
        Assertions.assertDoesNotThrow(() -> category.validate(new ThrowsValidationHandler()));

        final var updatedAt = category.getUpdatedAt();
        final var createdAt = category.getCreatedAt();

        Assertions.assertFalse(category.isActive());
        Assertions.assertNotNull(category.getDeletedAt());

        final var actualCategory = category.activate();

        Assertions.assertDoesNotThrow(() -> actualCategory.validate(new ThrowsValidationHandler()));
        Assertions.assertEquals(category.getId(), actualCategory.getId());
        Assertions.assertEquals(expectedName, actualCategory.getName());
        Assertions.assertEquals(expectedDescription, actualCategory.getDescription());
        Assertions.assertEquals(expectedIsActive, actualCategory.isActive());
        Assertions.assertEquals(createdAt, actualCategory.getCreatedAt());
        Assertions.assertTrue(actualCategory.getUpdatedAt().isAfter(updatedAt));
        Assertions.assertNull(actualCategory.getDeletedAt());
    }

    @Test
    public void givenAValidCategory_whenCallUpdate_thenReturnCategoryUpdated() {
        final var expectedName = "Filmes";
        final var expectedDescription = "a categoria mais assistida";
        final var expectedIsActive = true;

        final var category = Category.newCategory("Film", "a categori", expectedIsActive);
        Assertions.assertDoesNotThrow(() -> category.validate(new ThrowsValidationHandler()));

        final var updatedAt = category.getUpdatedAt();
        final var createdAt = category.getCreatedAt();

        final var actualCategory = category.update(expectedName, expectedDescription, expectedIsActive);

        Assertions.assertDoesNotThrow(() -> actualCategory.validate(new ThrowsValidationHandler()));
        Assertions.assertEquals(category.getId(), actualCategory.getId());
        Assertions.assertEquals(expectedName, actualCategory.getName());
        Assertions.assertEquals(expectedDescription, actualCategory.getDescription());
        Assertions.assertEquals(expectedIsActive, actualCategory.isActive());
        Assertions.assertEquals(createdAt, actualCategory.getCreatedAt());
        Assertions.assertTrue(actualCategory.getUpdatedAt().isAfter(updatedAt));
        Assertions.assertNull(actualCategory.getDeletedAt());
    }

    @Test
    public void givenAValidCategory_whenCallUpdateToInactive_thenReturnCategoryUpdated() {
        final var expectedName = "Filmes";
        final var expectedDescription = "a categoria mais assistida";
        final var expectedIsActive = false;

        final var category = Category.newCategory("Film", "a categori", true);
        Assertions.assertDoesNotThrow(() -> category.validate(new ThrowsValidationHandler()));
        Assertions.assertTrue(category.isActive());
        Assertions.assertNull(category.getDeletedAt());


        final var updatedAt = category.getUpdatedAt();
        final var createdAt = category.getCreatedAt();

        final var actualCategory = category.update(expectedName, expectedDescription, expectedIsActive);

        Assertions.assertDoesNotThrow(() -> actualCategory.validate(new ThrowsValidationHandler()));
        Assertions.assertEquals(category.getId(), actualCategory.getId());
        Assertions.assertEquals(expectedName, actualCategory.getName());
        Assertions.assertEquals(expectedDescription, actualCategory.getDescription());
        Assertions.assertFalse(category.isActive());
        Assertions.assertNotNull(category.getDeletedAt());
        Assertions.assertEquals(createdAt, actualCategory.getCreatedAt());
        Assertions.assertTrue(actualCategory.getUpdatedAt().isAfter(updatedAt));
        Assertions.assertNotNull(actualCategory.getDeletedAt());
    }

    @Test
    public void givenAValidCategory_whenCallUpdateToWithInvalidParame_thenReturnCategoryUpdated() {
        final String expectedName = null;
        final var expectedDescription = "a categoria mais assistida";
        final var expectedIsActive = true;

        final var category = Category.newCategory("Filmes", "a categoria mais assistida", expectedIsActive);
        Assertions.assertDoesNotThrow(() -> category.validate(new ThrowsValidationHandler()));


        final var updatedAt = category.getUpdatedAt();
        final var createdAt = category.getCreatedAt();

        final var actualCategory = category.update(expectedName, expectedDescription, expectedIsActive);

        Assertions.assertEquals(category.getId(), actualCategory.getId());
        Assertions.assertEquals(expectedName, actualCategory.getName());
        Assertions.assertEquals(expectedDescription, actualCategory.getDescription());
        Assertions.assertTrue(category.isActive());
        Assertions.assertEquals(createdAt, actualCategory.getCreatedAt());
        Assertions.assertTrue(actualCategory.getUpdatedAt().isAfter(updatedAt));
        Assertions.assertNull(actualCategory.getDeletedAt());
    }
}