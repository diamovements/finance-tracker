package org.example;

import org.example.client.UserClient;
import org.example.dto.UserDto;
import org.example.dto.request.AddCategoryRequest;
import org.example.entity.Category;
import org.example.exception.CategoryNotFoundException;
import org.example.service.CategoryCacheService;
import org.example.service.CategoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.containers.PostgreSQLContainer;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;


@Testcontainers
@SpringBootTest
@AutoConfigureMockMvc
public class CategoryIntegrationTest {

    @Container
    public static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:latest")
            .withDatabaseName("test")
            .withUsername("user")
            .withPassword("password");;

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
        registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
    }

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CategoryCacheService cacheService;

    @MockBean
    private CategoryService categoryService;

    @MockBean
    private UserClient userClient;

    private final UUID userId = UUID.randomUUID();
    private final String token = "Bearer token";


    @BeforeEach
    public void setUp() {
        Mockito.when(userClient.getUserByToken(eq(token))).thenReturn(userId);
    }

    @Test
    public void getAllCategoriesTest_shouldReturnAllCategories() throws Exception {
        Category category = mock(Category.class);
        String name = "Рестораны";

        Mockito.when(category.getName()).thenReturn(name);
        Mockito.when(cacheService.getAllCategoriesCached(userId)).thenReturn(List.of(category));
        Mockito.when(categoryService.getAllCategories(userId)).thenReturn(List.of(category));

        mockMvc.perform(get("/api/v1/categories/all")
                .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value(name));
    }

    @Test
    public void getCategoryByNameTest_shouldReturnCategory() throws Exception {
        Category category = mock(Category.class);
        String name = "Рестораны";

        Mockito.when(category.getName()).thenReturn(name);
        Mockito.when(categoryService.getCategoryByName(eq(name), eq(userId)))
                .thenReturn(Optional.of(category));

        mockMvc.perform(get("/api/v1/categories/{name}", name)
                .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(name));
    }

    @Test
    public void addCategoryTest_shouldAddCategory() throws Exception {
        UserDto data = mock(UserDto.class);

        Mockito.when(userClient.getUserData(token)).thenReturn(data);
        Mockito.doNothing().when(categoryService).addCustomCategory(eq(userId), any(AddCategoryRequest.class), eq(data));

        mockMvc.perform(post("/api/v1/categories/add")
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\": \"Музеи\"}"))
                .andExpect(status().isOk())
                .andExpect(content().string("Category added"));
    }

    @Test
    public void addCategoryTest_shouldThrowException() throws Exception {
        UserDto data = mock(UserDto.class);
        Category category = mock(Category.class);

        Mockito.when(userClient.getUserData(token)).thenReturn(data);
        Mockito.when(cacheService.getAllCategoriesCached(userId)).thenReturn(List.of(category));
        Mockito.doThrow(new IllegalArgumentException("Category exists"))
                .when(categoryService).addCustomCategory(eq(userId), any(AddCategoryRequest.class), eq(data));

        mockMvc.perform(post("/api/v1/categories/add")
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\": \"Рестораны\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Произошла ошибка: Category exists"));
    }

    @Test
    public void getCategoryByNameTest_shouldThrowException() throws Exception {
        String name = "Кафе";

        Mockito.doThrow(new CategoryNotFoundException("Category not found"))
                        .when(categoryService).getCategoryByName(any(String.class), eq(userId));

        mockMvc.perform(get("/api/v1/categories/{name}", name)
                        .header("Authorization", token))
                .andExpect(status().isNotFound());
    }
}
