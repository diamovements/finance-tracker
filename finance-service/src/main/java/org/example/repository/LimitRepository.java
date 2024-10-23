package org.example.repository;

import org.example.entity.Limit;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface LimitRepository extends CrudRepository<Limit, UUID> {
    List<Limit> findByUserId(UUID userId);
}
