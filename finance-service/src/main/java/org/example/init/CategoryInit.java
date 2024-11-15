package org.example.init;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.client.UserClient;
import org.example.entity.Category;
import org.example.repository.CategoryRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Component
@Slf4j
public class CategoryInit implements CommandLineRunner {

    private final CategoryRepository categoryRepository;
    private final static List<String> STANDARD_CATEGORIES = List.of("Аптеки", "Одежда", "Рестораны", "Супермаркеты", "Услуги", "Кредиты");

    @Override
    public void run(String... args) {
        for (String category : STANDARD_CATEGORIES) {
            if (!categoryRepository.existsByNameAndStandard(category, true)) {
                Category stCategory = new Category();
                stCategory.setName(category);
                stCategory.setStandard(true);
                categoryRepository.save(stCategory);
            }
        }
    }
}
