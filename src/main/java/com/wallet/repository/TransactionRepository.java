package com.wallet.repository;

import com.wallet.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, UUID> {
    
    // Used to check for Idempotency
    Optional<Transaction> findByReferenceId(String referenceId);
}