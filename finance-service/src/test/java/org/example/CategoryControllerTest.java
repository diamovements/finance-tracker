package org.example;

import org.example.entity.Category;
import org.example.service.CategoryService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;



@SpringBootTest
@AutoConfigureMockMvc
public class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CategoryService categoryService;

    @Test
    public void getAllCategoriesTest() throws Exception {
        List<Category> categories = new ArrayList<>();
        Category category = new Category();
        category.setName("Test Category");
        category.setUserId(UUID.fromString("00000000-0000-0000-0000-000000000000"));
        categories.add(category);
        UUID userId = UUID.fromString("00000000-0000-0000-0000-000000000000");

        Mockito.when(categoryService.getAllCategories(ArgumentMatchers.any(UUID.class))).thenReturn(categories);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/categories/all")
                        .param("userId", userId.toString()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].name").value("Test Category"));
    }

    @Test
    public void addCustomCategoryTest() throws Exception {
        UUID userId = UUID.fromString("00000000-0000-0000-0000-000000000000");
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/categories/add")
                        .param("name", "New Category")
                        .param("userId", userId.toString()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("Category added"));
    }

    @Test
    public void getCategoryByNameTest() throws Exception {
        UUID userId = UUID.fromString("00000000-0000-0000-0000-000000000000");
        Category category = new Category();
        category.setName("food");
        category.setUserId(UUID.fromString("00000000-0000-0000-0000-000000000000"));
        Mockito.when(categoryService.getCategoryByName(ArgumentMatchers.eq("food"), ArgumentMatchers.any(UUID.class)))
                .thenReturn(Optional.of(category));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/categories/food")
                        .param("userId", userId.toString()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("food"));
    }
}

