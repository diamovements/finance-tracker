package org.example.repository;

import org.example.entity.RecurringPayment;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RecurringPaymentRepository extends CrudRepository<RecurringPayment, UUID> {
    List<RecurringPayment> findByUserId(UUID userId);
    Optional<RecurringPayment> findByUserIdAndName(UUID userId, String name);
}
