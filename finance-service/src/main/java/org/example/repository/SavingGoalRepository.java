package org.example.repository;

import org.example.entity.SavingGoal;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SavingGoalRepository extends CrudRepository<SavingGoal, UUID> {
    List<SavingGoal> findByUserId(UUID userId);
}
