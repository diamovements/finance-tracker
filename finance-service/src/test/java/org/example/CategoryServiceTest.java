package org.example;


import org.example.client.UserClient;
import org.example.dto.NotificationMessage;
import org.example.dto.UserDto;
import org.example.dto.request.AddCategoryRequest;
import org.example.entity.Category;
import org.example.entity.EventType;
import org.example.repository.CategoryRepository;
import org.example.service.CategoryCacheService;
import org.example.service.CategoryService;
import org.example.service.KafkaProducerService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;

public class CategoryServiceTest {

    private final UUID userId = UUID.randomUUID();

    private CategoryService categoryService;

    @Mock
    private CategoryCacheService cacheService;

    @Mock
    private CacheManager cacheManager;

    @Mock
    private KafkaProducerService kafkaProducerService;

    @Mock
    private CategoryRepository categoryRepository;


    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        categoryService = new CategoryService(
                categoryRepository,
                cacheService,
                cacheManager,
                kafkaProducerService);
    }

    @Test
    public void getAllCategoriesTest_shouldReturnCategories() {
        Category category = mock(Category.class);

        Mockito.when(categoryService.getAllCategories(userId)).thenReturn(List.of(category));

        assertEquals(List.of(category), categoryService.getAllCategories(userId));
    }

    @Test
    public void addCustomCategoryTest_shouldAddCategory() {
        AddCategoryRequest request = new AddCategoryRequest("Test");
        UserDto data = mock(UserDto.class);

        Mockito.when(categoryRepository.existsByNameAndUserId(request.name(), userId)).thenReturn(false);
        Mockito.when(categoryRepository.existsByNameAndStandard(request.name(), true)).thenReturn(false);
        Cache cache = mock(Cache.class);
        Mockito.when(cacheManager.getCache("categories")).thenReturn(cache);

        categoryService.addCustomCategory(userId, request, data);

        ArgumentCaptor<Category> captor = ArgumentCaptor.forClass(Category.class);
        Mockito.verify(categoryRepository).save(captor.capture());
        Assertions.assertEquals(request.name(), captor.getValue().getName());
        ArgumentCaptor<NotificationMessage> captor2 = ArgumentCaptor.forClass(NotificationMessage.class);
        Mockito.verify(kafkaProducerService).sendMessage(captor2.capture());
        Assertions.assertEquals(data.email(), captor2.getValue().email());
        Assertions.assertEquals(EventType.CATEGORY_ADDED, captor2.getValue().eventType());
    }

    @Test
    public void addCustomCategoryTest_shouldThrowException() {
        AddCategoryRequest request = new AddCategoryRequest("Test");
        UserDto data = mock(UserDto.class);

        Mockito.when(categoryRepository.existsByNameAndUserId(request.name(), userId)).thenReturn(true);

        Assertions.assertThrows(IllegalArgumentException.class, () -> categoryService.addCustomCategory(userId, request, data));
        Mockito.verify(categoryRepository, never()).save(any());
        Mockito.verify(kafkaProducerService, never()).sendMessage(any());
    }

    @Test
    public void getCategoryByNameTest_shouldReturnUserCategory() {
        Category category = mock(Category.class);

        Mockito.when(categoryRepository.findByNameAndUserId(category.getName(), userId)).thenReturn(Optional.of(category));

        Optional<Category> res = categoryService.getCategoryByName(category.getName(), userId);

        Assertions.assertTrue(res.isPresent());
        Assertions.assertEquals(category.getName(), res.get().getName());
        Mockito.verify(categoryRepository, times(1)).findByNameAndUserId(category.getName(), userId);

    }

    @Test
    public void getCategoryByNameTest_shouldReturnStandardCategory() {
        Category category = mock(Category.class);

        Mockito.when(category.getName()).thenReturn("Test");
        Mockito.when(categoryRepository.findByNameAndStandardIsTrue(category.getName())).thenReturn(Optional.of(category));

        Optional<Category> res = categoryService.getCategoryByName(category.getName(), userId);

        Assertions.assertTrue(res.isPresent());
        Assertions.assertEquals(category.getName(), res.get().getName());
        Mockito.verify(categoryRepository, times(1)).findByNameAndStandardIsTrue(category.getName());
    }
}
