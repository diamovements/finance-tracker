package org.example.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.client.UserClient;
import org.example.dto.UserDto;
import org.example.dto.request.AddCategoryRequest;
import org.example.entity.Category;
import org.example.service.CategoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/categories")
@Slf4j
public class CategoryController {
    private final CategoryService categoryService;
    private final static String TOKEN = "Authorization";
    private final UserClient userClient;

    @GetMapping("/all")
    public List<Category> getAllCategories(@RequestHeader(TOKEN) String token) {
        UUID userId = userClient.getUserByToken(token);
        return categoryService.getAllCategories(userId);
    }

    @PostMapping("/add")
    public ResponseEntity<String> addCustomCategory(@RequestHeader(TOKEN) String token, @RequestBody AddCategoryRequest request) {
        UUID userId = userClient.getUserByToken(token);
        UserDto data = userClient.getUserData(token);
        categoryService.addCustomCategory(userId, request, data);
        return ResponseEntity.ok("Category added");
    }

    @GetMapping("/{name}")
    public Category getCategoryByName(@RequestHeader(TOKEN) String token, @PathVariable("name") String name) {
        UUID userId = userClient.getUserByToken(token);
        return categoryService.getCategoryByName(name, userId).orElseThrow(() -> new IllegalArgumentException("Category not found"));
    }
}
