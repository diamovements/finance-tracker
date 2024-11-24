package org.example.repository;

import org.example.entity.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@RepositoryRestResource(exported = false)
@Repository
public interface TransactionRepository extends CrudRepository<Transaction, UUID> {
    List<Transaction> findAllByUserId(UUID userId);
    Page<Transaction> findByUserId(UUID userId, Pageable pageable);
}
