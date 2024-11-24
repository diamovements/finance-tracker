package org.example.repository;

import org.example.entity.SavingGoal;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@RepositoryRestResource(exported = false)
@Repository
public interface SavingGoalRepository extends CrudRepository<SavingGoal, UUID> {
    List<SavingGoal> findByUserId(UUID userId);
}
