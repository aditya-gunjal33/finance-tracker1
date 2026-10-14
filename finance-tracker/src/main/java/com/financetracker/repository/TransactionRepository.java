package com.financetracker.repository;

import com.financetracker.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    /** Return all transactions ordered by creation date descending (newest first). */
    List<Transaction> findAllByOrderByCreatedAtDesc();
}
