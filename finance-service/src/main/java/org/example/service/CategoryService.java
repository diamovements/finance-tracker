package org.example.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.client.UserClient;
import org.example.dto.NotificationMessage;
import org.example.dto.UserDto;
import org.example.dto.request.AddCategoryRequest;
import org.example.entity.Category;
import org.example.entity.EventType;
import org.example.repository.CategoryRepository;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryService{

    private final CategoryRepository categoryRepository;
    private final CategoryCacheService cacheService;
    private final CacheManager cacheManager;
    private final KafkaProducerService kafkaProducerService;

    public List<Category> getAllCategories(UUID userId) {
        log.info("Getting all categories from cache");
        return cacheService.getAllCategoriesCached(userId);
    }

    public void addCustomCategory(UUID userId, AddCategoryRequest request, UserDto data) {
        boolean exists = categoryRepository.existsByNameAndUserId(request.name(), userId) ||
                categoryRepository.existsByNameAndStandard(request.name(), true);
        log.info("Category exists: {}", exists);
        if (exists) {
            log.error("Category already exists: {}", request.name());
            throw new IllegalArgumentException("Категория с таким именем уже есть.");
        }

        Category category = new Category();
        category.setName(request.name());
        category.setUserId(userId);
        category.setStandard(false);
        log.info("New category saved: {}", category.getName());

        categoryRepository.save(category);
        kafkaProducerService.sendMessage(new NotificationMessage(userId, data.email(),
                EventType.CATEGORY_ADDED, request.name(), null, null, null, null));

        Objects.requireNonNull(cacheManager.getCache("categories")).evict(userId);
    }

    public Optional<Category> getCategoryByName(String name, UUID userId) {
        Optional<Category> userCategory = categoryRepository.findByNameAndUserId(name, userId);
        return userCategory.isPresent() ? userCategory : categoryRepository.findByNameAndStandardIsTrue(name);
    }
}
