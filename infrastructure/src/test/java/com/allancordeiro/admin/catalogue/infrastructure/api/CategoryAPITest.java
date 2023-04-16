package com.allancordeiro.admin.catalogue.infrastructure.api;

import com.allancordeiro.admin.catalogue.ControllerTest;
import com.allancordeiro.admin.catalogue.application.category.create.CreateCategoryCommand;
import com.allancordeiro.admin.catalogue.application.category.create.CreateCategoryOutput;
import com.allancordeiro.admin.catalogue.application.category.create.CreateCategoryUseCase;
import com.allancordeiro.admin.catalogue.application.category.delete.DeleteCategoryUseCase;
import com.allancordeiro.admin.catalogue.application.category.retrieve.get.CategoryOutput;
import com.allancordeiro.admin.catalogue.application.category.retrieve.get.GetCategoryByIdUseCase;
import com.allancordeiro.admin.catalogue.application.category.retrieve.list.CategoryListOutput;
import com.allancordeiro.admin.catalogue.application.category.retrieve.list.ListCategoriesUseCase;
import com.allancordeiro.admin.catalogue.application.category.update.UpdateCategoryCommand;
import com.allancordeiro.admin.catalogue.application.category.update.UpdateCategoryOutput;
import com.allancordeiro.admin.catalogue.application.category.update.UpdateCategoryUseCase;
import com.allancordeiro.admin.catalogue.domain.category.Category;
import com.allancordeiro.admin.catalogue.domain.category.CategoryId;
import com.allancordeiro.admin.catalogue.domain.category.CategorySearchQuery;
import com.allancordeiro.admin.catalogue.domain.exceptions.DomainException;
import com.allancordeiro.admin.catalogue.domain.exceptions.NotFoundException;
import com.allancordeiro.admin.catalogue.domain.pagination.Pagination;
import com.allancordeiro.admin.catalogue.domain.validation.Error;
import com.allancordeiro.admin.catalogue.domain.validation.handler.Notification;
import com.allancordeiro.admin.catalogue.infrastructure.category.models.CreateCategoryRequest;
import com.allancordeiro.admin.catalogue.infrastructure.category.models.UpdateCategoryRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.vavr.API;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.List;

@ControllerTest(controllers = CategoryAPI.class)
public class CategoryAPITest {
    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;
    @MockBean
    private CreateCategoryUseCase createCategoryUseCase;
    @MockBean
    private GetCategoryByIdUseCase  getCategoryByIdUseCase;
    @MockBean
    private UpdateCategoryUseCase updateCategoryUseCase;
    @MockBean
    private DeleteCategoryUseCase deleteCategoryUseCase;

    @MockBean
    private ListCategoriesUseCase listCategoriesUseCase;

    @Test
    public void givenAValidCommand_whenCallsCreateCategory_shouldReturnCategoryId() throws Exception {
        final var expectedName = "Filmes";
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = true;

        final var input = new CreateCategoryRequest(expectedName, expectedDescription, expectedIsActive);

        Mockito.when(createCategoryUseCase.execute(Mockito.any()))
                .thenReturn(API.Right(CreateCategoryOutput.from("123")));

        final var request = MockMvcRequestBuilders.post("/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(input));

        this.mvc.perform(request)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.header().string("Location", "/categories/123"))
                .andExpect(MockMvcResultMatchers.header().string(
                        "Content-Type", MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", Matchers.equalTo("123")));

        final var captor = ArgumentCaptor.forClass(CreateCategoryCommand.class);
        Mockito.verify(createCategoryUseCase, Mockito.times(1)).execute(captor.capture());
        final var insertedCategory = captor.getValue();

        Assertions.assertEquals(expectedName, insertedCategory.name());
        Assertions.assertEquals(expectedDescription, insertedCategory.description());
        Assertions.assertEquals(expectedIsActive, insertedCategory.isActive());
    }

    @Test
    public void givenAnIvalidName_whenCallsCreateCategory_shouldReturnNotificationError() throws Exception {
        final String expectedName = null;
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = true;
        final var expectedMessage = "'name' should not be null";

        final var input = new CreateCategoryRequest(expectedName, expectedDescription, expectedIsActive);

        Mockito.when(createCategoryUseCase.execute(Mockito.any()))
                .thenReturn(API.Left(Notification.create(new Error(expectedMessage))));

        final var request = MockMvcRequestBuilders.post("/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(input));

        this.mvc.perform(request)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isUnprocessableEntity())
                .andExpect(MockMvcResultMatchers.header().string("Location", Matchers.nullValue()))
                .andExpect(MockMvcResultMatchers.header().string(
                        "Content-Type", MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors", Matchers.hasSize(1)))
                .andExpect(MockMvcResultMatchers.jsonPath(
                        "$.errors[0].message", Matchers.equalTo(expectedMessage)));

        final var captor = ArgumentCaptor.forClass(CreateCategoryCommand.class);
        Mockito.verify(createCategoryUseCase, Mockito.times(1)).execute(captor.capture());
        final var insertedCategory = captor.getValue();

        Assertions.assertEquals(expectedName, insertedCategory.name());
        Assertions.assertEquals(expectedDescription, insertedCategory.description());
        Assertions.assertEquals(expectedIsActive, insertedCategory.isActive());
    }

    @Test
    public void givenAnIvalidCommand_whenCallsCreateCategory_shouldReturnDomainException() throws Exception {
        final String expectedName = null;
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = true;
        final var expectedMessage = "'name' should not be null";

        final var input = new CreateCategoryRequest(expectedName, expectedDescription, expectedIsActive);

        Mockito.when(createCategoryUseCase.execute(Mockito.any()))
                .thenThrow(DomainException.with(new Error(expectedMessage)));

        final var request = MockMvcRequestBuilders.post("/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(input));

        this.mvc.perform(request)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isUnprocessableEntity())
                .andExpect(MockMvcResultMatchers.header().string("Location", Matchers.nullValue()))
                .andExpect(MockMvcResultMatchers.header().string(
                        "Content-Type", MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors", Matchers.hasSize(1)))
                .andExpect(MockMvcResultMatchers.jsonPath(
                        "$.errors[0].message", Matchers.equalTo(expectedMessage)));

        final var captor = ArgumentCaptor.forClass(CreateCategoryCommand.class);
        Mockito.verify(createCategoryUseCase, Mockito.times(1)).execute(captor.capture());
        final var insertedCategory = captor.getValue();

        Assertions.assertEquals(expectedName, insertedCategory.name());
        Assertions.assertEquals(expectedDescription, insertedCategory.description());
        Assertions.assertEquals(expectedIsActive, insertedCategory.isActive());
    }

    @Test
    public void givenAValidId_whenCallsGetCategory_shouldReturnCategory() throws Exception {
        final var expectedName = "Filmes";
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = true;

        final var category = Category.newCategory(expectedName, expectedDescription, expectedIsActive);
        final var expectedId = category.getId().getValue();

        Mockito.when(getCategoryByIdUseCase.execute(Mockito.any()))
                .thenReturn(CategoryOutput.from(category));


        final var request = MockMvcRequestBuilders.get("/categories/{id}", expectedId)
                .contentType(MediaType.APPLICATION_JSON);

        this.mvc.perform(request)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", Matchers.equalTo(expectedId)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name", Matchers.equalTo(expectedName)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.description", Matchers.equalTo(expectedDescription)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.is_active", Matchers.equalTo(expectedIsActive)))
                .andExpect(MockMvcResultMatchers.jsonPath(
                        "$.created_at", Matchers.equalTo(category.getCreatedAt().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath(
                        "$.updated_at", Matchers.equalTo(category.getUpdatedAt().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath(
                        "$.deleted_at", Matchers.equalTo(category.getDeletedAt())));

        Mockito.verify(getCategoryByIdUseCase, Mockito.times(1))
                .execute(Mockito.eq(expectedId));
    }

    @Test
    public void givenAnInvalidId_whenCallsGetCategory_shouldReturnNotFound() throws Exception {
        final var expectedErrorMessage = "Category with ID 123 was not found";
        final var expectedId = CategoryId.from("123");

        Mockito.when(getCategoryByIdUseCase.execute(Mockito.any()))
                .thenThrow(NotFoundException.with(Category.class, expectedId));


        final var request = MockMvcRequestBuilders.get("/categories/{id}", expectedId)
                .contentType(MediaType.APPLICATION_JSON);

        this.mvc.perform(request)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath(
                        "$.message",
                        Matchers.equalTo(expectedErrorMessage))
                );
    }

    @Test
    public void givenAValidCommand_whenCallsUpdateCategory_thenShouldReturnCategoryId() throws Exception {
        final var expectedId = "123";
        final var expectedName = "Filmes";
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = true;

        Mockito.when(updateCategoryUseCase.execute(Mockito.any()))
                .thenReturn(API.Right(UpdateCategoryOutput.from(expectedId)));

        final var command = new UpdateCategoryRequest(expectedName, expectedDescription, expectedIsActive);
        final var request = MockMvcRequestBuilders.put("/categories/{id}", expectedId)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(command));

        this.mvc.perform(request)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.header().string(
                        "Content-Type", MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", Matchers.equalTo(expectedId)));

        final var captor = ArgumentCaptor.forClass(UpdateCategoryCommand.class);
        Mockito.verify(updateCategoryUseCase, Mockito.times(1)).execute(captor.capture());
        final var updatedCategory = captor.getValue();

        Assertions.assertEquals(expectedName, updatedCategory.name());
        Assertions.assertEquals(expectedDescription, updatedCategory.description());
        Assertions.assertEquals(expectedIsActive, updatedCategory.isActive());
    }

    @Test
    public void givenACommandWithInvalidId_whenCallsUpdateCategory_shouldReturnNotFoundException() throws Exception {
        final var expectedId = "not-found";
        final var expectedName = "Filmes";
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = true;
        final var expectedErrorMessage = "Category with ID not-found was not found";

        Mockito.when(updateCategoryUseCase.execute(Mockito.any()))
                .thenThrow(NotFoundException.with(Category.class, CategoryId.from(expectedId)));

        final var command = new UpdateCategoryRequest(expectedName, expectedDescription, expectedIsActive);
        final var request = MockMvcRequestBuilders.put("/categories/{id}", expectedId)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(command));

        this.mvc.perform(request)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.header().string(
                        "Content-Type", MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.jsonPath(
                        "$.message", Matchers.equalTo(expectedErrorMessage)));

        final var captor = ArgumentCaptor.forClass(UpdateCategoryCommand.class);
        Mockito.verify(updateCategoryUseCase, Mockito.times(1)).execute(captor.capture());
        final var updatedCategory = captor.getValue();

        Assertions.assertEquals(expectedName, updatedCategory.name());
        Assertions.assertEquals(expectedDescription, updatedCategory.description());
        Assertions.assertEquals(expectedIsActive, updatedCategory.isActive());
    }

    @Test
    public void givenAnInvalidName_whenCallsUpdateCategory_thenShouldReturnDomainException() throws Exception {
        final var expectedId = "123";
        final var expectedName = "Filmes";
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = true;
        final var expectedErrorMessage = "'name' should not be null";

        Mockito.when(updateCategoryUseCase.execute(Mockito.any()))
                .thenReturn(API.Left(Notification.create(new Error(expectedErrorMessage))));

        final var command = new UpdateCategoryRequest(expectedName, expectedDescription, expectedIsActive);
        final var request = MockMvcRequestBuilders.put("/categories/{id}", expectedId)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(command));

        this.mvc.perform(request)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isUnprocessableEntity())
                .andExpect(MockMvcResultMatchers.header().string("Location", Matchers.nullValue()))
                .andExpect(MockMvcResultMatchers.header().string(
                        "Content-Type", MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors", Matchers.hasSize(1)))
                .andExpect(MockMvcResultMatchers.jsonPath(
                        "$.errors[0].message", Matchers.equalTo(expectedErrorMessage)));

        final var captor = ArgumentCaptor.forClass(UpdateCategoryCommand.class);
        Mockito.verify(updateCategoryUseCase, Mockito.times(1)).execute(captor.capture());
        final var updatedCategory = captor.getValue();

        Assertions.assertEquals(expectedName, updatedCategory.name());
        Assertions.assertEquals(expectedDescription, updatedCategory.description());
        Assertions.assertEquals(expectedIsActive, updatedCategory.isActive());
    }

    @Test
    public void givenAValidId_whenCallsDeleteCategory_shouldBeOK() throws Exception {
        final var expectedId = "123";

        Mockito.doNothing()
                .when(deleteCategoryUseCase).execute(Mockito.any());


        final var request = MockMvcRequestBuilders.delete("/categories/{id}", expectedId)
                .contentType(MediaType.APPLICATION_JSON);

        this.mvc.perform(request)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNoContent());

        Mockito.verify(deleteCategoryUseCase, Mockito.times(1))
                .execute(Mockito.eq(expectedId));
    }

    @Test
    public void givenAValidParams_whenCallsListCategories_shouldReturnCategories() throws Exception {
        final var category = Category.newCategory("Movies", "movies", true);
        final var expectedPage = 0;
        final var expectedPerPage = 10;
        final var expectedTerms = "movies";
        final var expectedSort = "description";
        final var expectedDirection = "desc";
        final var expectedItemsCount = 1;
        final var expectedTotal = 1;
        final var expectedItems = List.of(CategoryListOutput.from(category));



        Mockito.when(listCategoriesUseCase.execute(Mockito.any()))
                .thenReturn(new Pagination<>(expectedPage, expectedPerPage, expectedTotal, expectedItems));


        final var request = MockMvcRequestBuilders.get("/categories")
                .queryParam("page", String.valueOf(expectedPage))
                .queryParam("perPage", String.valueOf(expectedPerPage))
                .queryParam("sort", expectedSort)
                .queryParam("dir", expectedDirection)
                .queryParam("search", expectedTerms)
                .contentType(MediaType.APPLICATION_JSON);

        this.mvc.perform(request)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.current_page", Matchers.equalTo(expectedPage)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.per_page", Matchers.equalTo(expectedPerPage)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.total", Matchers.equalTo(expectedTotal)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.items", Matchers.hasSize(expectedItemsCount)))
                .andExpect(
                        MockMvcResultMatchers.jsonPath("$.items[0].id",
                                Matchers.equalTo(category.getId().getValue()))
                )
                .andExpect(
                        MockMvcResultMatchers.jsonPath("$.items[0].name",
                                Matchers.equalTo(category.getName()))
                )
                .andExpect(
                        MockMvcResultMatchers.jsonPath("$.items[0].description",
                                Matchers.equalTo(category.getDescription()))
                )
                .andExpect(
                        MockMvcResultMatchers.jsonPath("$.items[0].is_active",
                                Matchers.equalTo(category.isActive()))
                )
                .andExpect(
                        MockMvcResultMatchers.jsonPath("$.items[0].created_at",
                                Matchers.equalTo(category.getCreatedAt().toString()))
                )
                .andExpect(
                        MockMvcResultMatchers.jsonPath("$.items[0].deleted_at",
                                Matchers.equalTo(category.getDeletedAt()))
                );


        final var captor = ArgumentCaptor.forClass(CategorySearchQuery.class);
        Mockito.verify(listCategoriesUseCase, Mockito.times(1)).execute(captor.capture());
        final var actualQuery = captor.getValue();

        Assertions.assertEquals(expectedPage, actualQuery.page());
        Assertions.assertEquals(expectedPerPage, actualQuery.perPage());
        Assertions.assertEquals(expectedDirection, actualQuery.direction());
        Assertions.assertEquals(expectedSort, actualQuery.sort());
        Assertions.assertEquals(expectedTerms, actualQuery.terms());

    }


}
