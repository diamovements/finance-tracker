package org.example.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.entity.Category;
import org.example.repository.CategoryRepository;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class CategoryCacheService {
    private final CategoryRepository categoryRepository;

    @Cacheable(value = "categories", key = "#p0")
    public List<Category> getAllCategoriesCached(UUID userId) {
        log.info("Getting all categories from cache");
        List<Category> userCategories = categoryRepository.findByUserId(userId);
        List<Category> standardCategories = categoryRepository.findByStandardIsTrue();
        log.debug("Standard categories: {}", standardCategories.stream().toList());
        List<Category> all = new ArrayList<>();
        all.addAll(standardCategories);
        all.addAll(userCategories);
        return all;
    }
}
