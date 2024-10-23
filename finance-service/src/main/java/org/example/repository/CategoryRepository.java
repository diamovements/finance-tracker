package org.example.repository;

import org.example.entity.Category;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CategoryRepository extends CrudRepository<Category, UUID> {
    List<Category> findByUserId(UUID userId);
    List<Category> findByIsStandardIsTrue();
    Optional<Category> findByNameAndUserId(String name, UUID userId);
    boolean existsByNameAndUserId(String name, UUID userId);
    boolean existsByNameAndIsStandard(String name, boolean isStandard);
}
