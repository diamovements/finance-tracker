package org.example.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/categories")
public class CategoryController {
    private final CategoryService categoryService;

    @GetMapping("/all")
    public List<Category> getAllCategories(@RequestParam("userId") UUID userId) {
        return categoryService.getAllCategories(userId);
    }

    @PostMapping("/add")
    public ResponseEntity<String> addCustomCategory(@RequestParam("userId") UUID userId, @RequestParam("name") String name) {
        categoryService.addCustomCategory(userId, name);
        return ResponseEntity.ok("Category added");
    }

    @GetMapping("/{name}")
    public Category getCategoryByName(@RequestParam("userId") UUID userId, @PathVariable("name") String name) {
        return categoryService.getCategoryByName(name, userId).get();
    }
}
