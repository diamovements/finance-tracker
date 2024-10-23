package org.example.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.entity.Category;
import org.example.repository.CategoryRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryService{

    private final CategoryRepository categoryRepository;

    public List<Category> getAllCategories(UUID userId) {
        List<Category> userCategories = categoryRepository.findByUserId(userId);
        log.info("User categories: {}", userCategories);

        List<Category> standardCategories = categoryRepository.findByIsStandardIsTrue();
        log.info("Standard categories: {}", standardCategories);

        List<Category> all = new ArrayList<>();
        all.addAll(userCategories);
        all.addAll(standardCategories);

        return all;
    }

    public void addCustomCategory(UUID userId, String name) {
        boolean exists = categoryRepository.existsByNameAndUserId(name, userId) ||
                categoryRepository.existsByNameAndIsStandard(name, true);
        if (exists) {
            log.error("Category already exists: {}", name);
            throw new IllegalArgumentException("Category already exists");
        }

        Category category = new Category();
        category.setName(name);
        category.setUserId(userId);
        category.setStandard(false);
        log.info("New category saved: {}", category);

        categoryRepository.save(category);
    }

    public Optional<Category> getCategoryByName(String name, UUID userId) {
        return categoryRepository.findByNameAndUserId(name, userId);
    }
}
