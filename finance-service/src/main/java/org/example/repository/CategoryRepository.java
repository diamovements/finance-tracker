package org.example.repository;

import org.example.entity.Category;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RepositoryRestResource(exported = false)
@Repository
public interface CategoryRepository extends CrudRepository<Category, UUID> {
    List<Category> findByUserId(UUID userId);
    Optional<Category> findByNameAndStandardIsTrue(String name);
    List<Category> findByStandardIsTrue();
    Optional<Category> findByNameAndUserId(String name, UUID userId);
    boolean existsByNameAndUserId(String name, UUID userId);
    boolean existsByNameAndStandard(String name, boolean isStandard);
}
