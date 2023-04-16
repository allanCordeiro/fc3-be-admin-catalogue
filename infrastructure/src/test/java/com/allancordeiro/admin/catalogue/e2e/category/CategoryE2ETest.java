package com.allancordeiro.admin.catalogue.e2e.category;

import com.allancordeiro.admin.catalogue.E2ETest;
import com.allancordeiro.admin.catalogue.domain.category.CategoryId;
import com.allancordeiro.admin.catalogue.infrastructure.category.models.CategoryResponse;
import com.allancordeiro.admin.catalogue.infrastructure.category.models.CreateCategoryRequest;
import com.allancordeiro.admin.catalogue.infrastructure.category.models.UpdateCategoryRequest;
import com.allancordeiro.admin.catalogue.infrastructure.category.persistence.CategoryRepository;
import com.allancordeiro.admin.catalogue.infrastructure.configuration.json.Json;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.shaded.org.hamcrest.Matchers;

@E2ETest
@Testcontainers
public class CategoryE2ETest {

    @Autowired
    private MockMvc mvc;
    @Autowired
    private CategoryRepository categoryRepository;

    @Container
    private static final MySQLContainer MY_SQL_CONTAINER =
            new MySQLContainer("mysql:latest")
            .withPassword("123456")
            .withUsername("root")
            .withDatabaseName("adm_videos");

    @DynamicPropertySource
    public static void setDatasourceProperties(final DynamicPropertyRegistry registry) {
        final var mappedPort = MY_SQL_CONTAINER.getMappedPort(3306);
        System.out.printf("Container is running on port %s\n", mappedPort);
        registry.add("mysql.port", () -> mappedPort);

    }

    @Test
    public void asACatalogAdminIShouldBeAbleToCreateANewCategoryWithValidValues() throws Exception {
        Assertions.assertTrue(MY_SQL_CONTAINER.isRunning());
        Assertions.assertEquals(0, categoryRepository.count());
        
        final var expectedName = "Filmes";
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = true;
        
        final var actualId = givenCategory(expectedName, expectedDescription, expectedIsActive);

        final var actualCategory = retrieveCategory(actualId.getValue());

        Assertions.assertEquals(expectedName, actualCategory.name());
        Assertions.assertEquals(expectedDescription, actualCategory.description());
        Assertions.assertEquals(expectedIsActive, actualCategory.active());
        Assertions.assertNotNull(actualCategory.createdAt());
        Assertions.assertNotNull(actualCategory.updatedAt());
        Assertions.assertNull(actualCategory.deletedAt());
    }

    @Test
    public void asACatalogAdminIShouldBeAbleToNavigateToAllCategories() throws Exception {
        Assertions.assertTrue(MY_SQL_CONTAINER.isRunning());
        Assertions.assertEquals(0, categoryRepository.count());

        givenCategory("Filmes", null, true);
        givenCategory("Documentários", null, true);
        givenCategory("Séries", null, true);

        listCategories(0, 1)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.current_page").value(0))
                .andExpect(MockMvcResultMatchers.jsonPath("$.per_page").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.total").value(3))
                .andExpect(MockMvcResultMatchers.jsonPath("$.items.length()").value(1))
                .andExpect(
                        MockMvcResultMatchers.jsonPath("$.items[0].name")
                                .value("Documentários")
                );

        listCategories(1, 1)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.current_page").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.per_page").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.total").value(3))
                .andExpect(MockMvcResultMatchers.jsonPath("$.items.length()").value(1))
                .andExpect(
                        MockMvcResultMatchers.jsonPath("$.items[0].name")
                                .value("Filmes")
                );

        listCategories(2, 1)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.current_page").value(2))
                .andExpect(MockMvcResultMatchers.jsonPath("$.per_page").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.total").value(3))
                .andExpect(MockMvcResultMatchers.jsonPath("$.items.length()").value(1))
                .andExpect(
                        MockMvcResultMatchers.jsonPath("$.items[0].name")
                                .value("Séries")
                );

        listCategories(3, 1)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.current_page").value(3))
                .andExpect(MockMvcResultMatchers.jsonPath("$.per_page").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.total").value(3))
                .andExpect(MockMvcResultMatchers.jsonPath("$.items.length()").value(0));
    }

    @Test
    public void asACatalogAdminIShouldBeAbleToSearchBetweenAllCategories() throws Exception {
        Assertions.assertTrue(MY_SQL_CONTAINER.isRunning());
        Assertions.assertEquals(0, categoryRepository.count());

        givenCategory("Filmes", null, true);
        givenCategory("Documentários", null, true);
        givenCategory("Séries", null, true);

        listCategories(0, 1, "fil")
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.current_page").value(0))
                .andExpect(MockMvcResultMatchers.jsonPath("$.per_page").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.total").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.items.length()").value(1))
                .andExpect(
                        MockMvcResultMatchers.jsonPath("$.items[0].name")
                                .value("Filmes")
                );
    }

    @Test
    public void asACatalogAdminIShouldBeAbleToSortAllCategoriesByDescriptionDesc() throws Exception {
        Assertions.assertTrue(MY_SQL_CONTAINER.isRunning());
        Assertions.assertEquals(0, categoryRepository.count());

        givenCategory("Filmes", "C", true);
        givenCategory("Documentários", "Z", true);
        givenCategory("Séries", "A", true);

        listCategories(0, 3, "", "description", "desc")
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.current_page").value(0))
                .andExpect(MockMvcResultMatchers.jsonPath("$.per_page").value(3))
                .andExpect(MockMvcResultMatchers.jsonPath("$.total").value(3))
                .andExpect(MockMvcResultMatchers.jsonPath("$.items.length()").value(3))
                .andExpect(
                        MockMvcResultMatchers.jsonPath("$.items[0].name")
                                .value("Documentários")
                )
                .andExpect(
                        MockMvcResultMatchers.jsonPath("$.items[1].name")
                                .value("Filmes")
                )
                .andExpect(
                        MockMvcResultMatchers.jsonPath("$.items[2].name")
                                .value("Séries")
                );
    }

    @Test
    public void asACatalogAdminIShouldBeAbleToGetACategoryByItsIdentifier() throws Exception {
        Assertions.assertTrue(MY_SQL_CONTAINER.isRunning());
        Assertions.assertEquals(0, categoryRepository.count());

        final var expectedName = "Filmes";
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = true;

        final var actualId = givenCategory(expectedName, expectedDescription, expectedIsActive);

        final var actualCategory = retrieveCategory(actualId.getValue());

        Assertions.assertEquals(expectedName, actualCategory.name());
        Assertions.assertEquals(expectedDescription, actualCategory.description());
        Assertions.assertEquals(expectedIsActive, actualCategory.active());
        Assertions.assertNotNull(actualCategory.createdAt());
        Assertions.assertNotNull(actualCategory.updatedAt());
        Assertions.assertNull(actualCategory.deletedAt());
    }

    @Test
    public void asACatalogAdminIShouldBeAbleToSeeATreatedErrorByGettingNotFoundCategory() throws Exception {
        Assertions.assertTrue(MY_SQL_CONTAINER.isRunning());
        Assertions.assertEquals(0, categoryRepository.count());


        final var request = MockMvcRequestBuilders.get("/categories/123")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON);

        this.mvc.perform(request)
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath(
                        "$.message").value("Category with ID 123 was not found"));
    }

    @Test
    public void asACatalogAdminIShouldBeAbleToUpdateACategoryByItsIdentifier() throws Exception {
        Assertions.assertTrue(MY_SQL_CONTAINER.isRunning());
        Assertions.assertEquals(0, categoryRepository.count());

        final var expectedName = "Filmes";
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = true;

        final var actualId = givenCategory("Movies", null, true);

        final var body = new UpdateCategoryRequest(expectedName, expectedDescription, expectedIsActive);

        final var request = MockMvcRequestBuilders.put("/categories/" + actualId.getValue())
                .contentType(MediaType.APPLICATION_JSON)
                .content(Json.writeValueAsString(body));

        this.mvc.perform(request)
                .andExpect(MockMvcResultMatchers.status().isOk());

        final var actualCategory = categoryRepository.findById(actualId.getValue()).get();


        Assertions.assertEquals(expectedName, actualCategory.getName());
        Assertions.assertEquals(expectedDescription, actualCategory.getDescription());
        Assertions.assertEquals(expectedIsActive, actualCategory.isActive());
        Assertions.assertNotNull(actualCategory.getCreatedAt());
        Assertions.assertNotNull(actualCategory.getUpdatedAt());
        Assertions.assertNull(actualCategory.getDeletedAt());
    }

    @Test
    public void asACatalogAdminIShouldBeAbleToInactivateCategoryByItsIdentifier() throws Exception {
        Assertions.assertTrue(MY_SQL_CONTAINER.isRunning());
        Assertions.assertEquals(0, categoryRepository.count());

        final var expectedName = "Filmes";
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = false;

        final var actualId = givenCategory(expectedName, expectedDescription, true);

        final var body = new UpdateCategoryRequest(expectedName, expectedDescription, expectedIsActive);

        final var request = MockMvcRequestBuilders.put("/categories/" + actualId.getValue())
                .contentType(MediaType.APPLICATION_JSON)
                .content(Json.writeValueAsString(body));

        this.mvc.perform(request)
                .andExpect(MockMvcResultMatchers.status().isOk());

        final var actualCategory = categoryRepository.findById(actualId.getValue()).get();


        Assertions.assertEquals(expectedName, actualCategory.getName());
        Assertions.assertEquals(expectedDescription, actualCategory.getDescription());
        Assertions.assertEquals(expectedIsActive, actualCategory.isActive());
        Assertions.assertNotNull(actualCategory.getCreatedAt());
        Assertions.assertNotNull(actualCategory.getUpdatedAt());
        Assertions.assertNotNull(actualCategory.getDeletedAt());
    }

    @Test
    public void asACatalogAdminIShouldBeAbleToReactivateCategoryByItsIdentifier() throws Exception {
        Assertions.assertTrue(MY_SQL_CONTAINER.isRunning());
        Assertions.assertEquals(0, categoryRepository.count());

        final var expectedName = "Filmes";
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = true;

        final var actualId = givenCategory(expectedName, expectedDescription, false);

        final var body = new UpdateCategoryRequest(expectedName, expectedDescription, expectedIsActive);

        final var request = MockMvcRequestBuilders.put("/categories/" + actualId.getValue())
                .contentType(MediaType.APPLICATION_JSON)
                .content(Json.writeValueAsString(body));

        this.mvc.perform(request)
                .andExpect(MockMvcResultMatchers.status().isOk());

        final var actualCategory = categoryRepository.findById(actualId.getValue()).get();


        Assertions.assertEquals(expectedName, actualCategory.getName());
        Assertions.assertEquals(expectedDescription, actualCategory.getDescription());
        Assertions.assertEquals(expectedIsActive, actualCategory.isActive());
        Assertions.assertNotNull(actualCategory.getCreatedAt());
        Assertions.assertNotNull(actualCategory.getUpdatedAt());
        Assertions.assertNull(actualCategory.getDeletedAt());
    }

    @Test
    public void asACatalogAdminIShouldBeAbleToDeleteACategoryByItsIdentifier() throws Exception {
        Assertions.assertTrue(MY_SQL_CONTAINER.isRunning());
        Assertions.assertEquals(0, categoryRepository.count());

        final var expectedName = "Filmes";
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = true;

        final var actualId = givenCategory(expectedName, expectedDescription, expectedIsActive);

        this.mvc.perform(MockMvcRequestBuilders.delete("/categories/" + actualId.getValue())
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().isNoContent());

        Assertions.assertFalse(this.categoryRepository.existsById(actualId.getValue()));
    }


    private ResultActions listCategories(final int page, final int perPage) throws Exception {
        return listCategories(page, perPage, "", "", "");
    }

    private ResultActions listCategories(final int page, final int perPage, final String search) throws Exception {
        return listCategories(page, perPage , search, "", "");
    }

    private ResultActions listCategories(
            final int page,
            final int perPage,
            final String search,
            final String sort,
            final String direction
    ) throws Exception {
        final var request = MockMvcRequestBuilders.get("/categories")
                .queryParam("page", String.valueOf(page))
                .queryParam("perPage", String.valueOf(perPage))
                .queryParam("search", search)
                .queryParam("sort", sort)
                .queryParam("dir", direction)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON);

        return this.mvc.perform(request);
    }

    private CategoryResponse retrieveCategory(final String id) throws Exception {
        final var request = MockMvcRequestBuilders.get("/categories/" + id)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON);

        final var json = this.mvc.perform(request)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn()
                .getResponse().getContentAsString();

        return Json.readValue(json, CategoryResponse.class);
    }

    private CategoryId givenCategory(final String name, final String description, final boolean isActive)
            throws Exception {
        final var body = new CreateCategoryRequest(name, description, isActive);

        final var request = MockMvcRequestBuilders.post("/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(Json.writeValueAsString(body));

        final var actualId = this.mvc.perform(request)
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andReturn()
                .getResponse().getHeader("Location")
                .replace("/categories/", "");

        return CategoryId.from(actualId);
    }
}
