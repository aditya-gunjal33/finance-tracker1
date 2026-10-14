package com.financetracker.service;

import com.financetracker.model.Transaction;
import com.financetracker.repository.TransactionRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class TransactionService {

    private final TransactionRepository repo;

    public TransactionService(TransactionRepository repo) {
        this.repo = repo;
    }

    /** Persist a new transaction. */
    public Transaction save(Transaction t) {
        return repo.save(t);
    }

    /** All transactions, newest first. */
    public List<Transaction> findAll() {
        return repo.findAllByOrderByCreatedAtDesc();
    }

    /** Delete by id; silently ignores if not found. */
    public void deleteById(Long id) {
        if (repo.existsById(id)) {
            repo.deleteById(id);
        }
    }

    /** Find by id. */
    public Optional<Transaction> findById(Long id) {
        return repo.findById(id);
    }

    /** Total income across all transactions. */
    public BigDecimal totalIncome() {
        return repo.findAllByOrderByCreatedAtDesc().stream()
                .filter(t -> "INCOME".equalsIgnoreCase(t.getType()))
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /** Total expenses across all transactions. */
    public BigDecimal totalExpenses() {
        return repo.findAllByOrderByCreatedAtDesc().stream()
                .filter(t -> "EXPENSE".equalsIgnoreCase(t.getType()))
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /** Balance = income − expenses. */
    public BigDecimal balance() {
        return totalIncome().subtract(totalExpenses());
    }
}
