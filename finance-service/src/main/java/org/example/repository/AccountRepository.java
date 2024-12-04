package org.example.repository;

import org.example.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

@RepositoryRestResource(exported = false)
@Repository
public interface AccountRepository extends JpaRepository<Account, UUID> {

    Optional<Account> findAccountByUserId(UUID userId);
    @Query("SELECT a.balance FROM Account a WHERE a.userId = :userId")
    BigDecimal findBalanceByUserId(@Param("userId") UUID userId);
}
