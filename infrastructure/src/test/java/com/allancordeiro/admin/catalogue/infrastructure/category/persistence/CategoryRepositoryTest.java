package com.allancordeiro.admin.catalogue.infrastructure.category.persistence;

import com.allancordeiro.admin.catalogue.domain.category.Category;
import com.allancordeiro.admin.catalogue.MySQLGatewayTest;
import org.hibernate.PropertyValueException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;

@MySQLGatewayTest
public class CategoryRepositoryTest {
    @Autowired
    private CategoryRepository categoryRepository;

    @Test
    public void givenAnInvalidName_whenCallsSave_shouldReturnError() {
        final var expectedPropertyName = "name";
        final var expectedErrorMessage = "not-null property references a null or transient value : com.allancordeiro.admin.catalogue.infrastructure.category.persistence.CategoryJpaEntity.name";
        final var category = Category.newCategory("Filmes", "A categoria mais assistida", true);
        final var entity = CategoryJpaEntity.from(category);
        entity.setName(null);

        final var actualException = Assertions
                .assertThrows(DataIntegrityViolationException.class, () -> categoryRepository.save(entity));

        final var actualCause = Assertions.assertInstanceOf(PropertyValueException.class, actualException.getCause());

        Assertions.assertEquals(expectedPropertyName, actualCause.getPropertyName());
        Assertions.assertEquals(expectedErrorMessage, actualCause.getMessage());

    }

    @Test
    public void givenAnInvalidCreatedAt_whenCallsSave_shouldReturnError() {
        final var expectedPropertyName = "createdAt";
        final var expectedErrorMessage = "not-null property references a null or transient value : com.allancordeiro.admin.catalogue.infrastructure.category.persistence.CategoryJpaEntity.createdAt";
        final var category = Category.newCategory("Filmes", "A categoria mais assistida", true);
        final var entity = CategoryJpaEntity.from(category);
        entity.setCreatedAt(null);

        final var actualException = Assertions
                .assertThrows(DataIntegrityViolationException.class, () -> categoryRepository.save(entity));

        final var actualCause = Assertions.assertInstanceOf(PropertyValueException.class, actualException.getCause());

        Assertions.assertEquals(expectedPropertyName, actualCause.getPropertyName());
        Assertions.assertEquals(expectedErrorMessage, actualCause.getMessage());

    }

    @Test
    public void givenAnInvalidUpdatedAt_whenCallsSave_shouldReturnError() {
        final var expectedPropertyName = "updatedAt";
        final var expectedErrorMessage = "not-null property references a null or transient value : com.allancordeiro.admin.catalogue.infrastructure.category.persistence.CategoryJpaEntity.updatedAt";
        final var category = Category.newCategory("Filmes", "A categoria mais assistida", true);
        final var entity = CategoryJpaEntity.from(category);
        entity.setUpdatedAt(null);

        final var actualException = Assertions
                .assertThrows(DataIntegrityViolationException.class, () -> categoryRepository.save(entity));

        final var actualCause = Assertions.assertInstanceOf(PropertyValueException.class, actualException.getCause());

        Assertions.assertEquals(expectedPropertyName, actualCause.getPropertyName());
        Assertions.assertEquals(expectedErrorMessage, actualCause.getMessage());

    }
}
