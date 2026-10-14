package com.financetracker.service;

import com.financetracker.model.Transaction;
import com.financetracker.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for TransactionService using an in-memory H2 database.
 * Each test runs inside a transaction that is rolled back after completion
 * so tests remain independent.
 */
@SpringBootTest
@Transactional
class TransactionServiceTest {

    @Autowired
    private TransactionService service;

    @Autowired
    private TransactionRepository repo;

    @BeforeEach
    void cleanUp() {
        repo.deleteAll();
    }

    // ── Save & Retrieve ──────────────────────────────────────────

    @Test
    @DisplayName("Save a transaction and retrieve it")
    void testSaveAndRetrieve() {
        Transaction t = new Transaction("Salary", new BigDecimal("50000.00"), "INCOME", "Salary");
        service.save(t);

        List<Transaction> all = service.findAll();
        assertThat(all).hasSize(1);
        assertThat(all.get(0).getTitle()).isEqualTo("Salary");
    }

    // ── Total Income ─────────────────────────────────────────────

    @Test
    @DisplayName("Total income sums only INCOME transactions")
    void testTotalIncome() {
        service.save(new Transaction("Salary",    new BigDecimal("50000"), "INCOME",  "Salary"));
        service.save(new Transaction("Freelance", new BigDecimal("10000"), "INCOME",  "Freelance"));
        service.save(new Transaction("Food",      new BigDecimal("500"),   "EXPENSE", "Food"));

        assertThat(service.totalIncome()).isEqualByComparingTo("60000");
    }

    // ── Total Expenses ───────────────────────────────────────────

    @Test
    @DisplayName("Total expenses sums only EXPENSE transactions")
    void testTotalExpenses() {
        service.save(new Transaction("Food",      new BigDecimal("500"),   "EXPENSE", "Food"));
        service.save(new Transaction("Transport", new BigDecimal("200"),   "EXPENSE", "Transport"));
        service.save(new Transaction("Salary",    new BigDecimal("50000"), "INCOME",  "Salary"));

        assertThat(service.totalExpenses()).isEqualByComparingTo("700");
    }

    // ── Balance ──────────────────────────────────────────────────

    @Test
    @DisplayName("Balance = income - expenses")
    void testBalance() {
        service.save(new Transaction("Salary", new BigDecimal("10000"), "INCOME",  "Salary"));
        service.save(new Transaction("Rent",   new BigDecimal("3000"),  "EXPENSE", "Rent"));

        assertThat(service.balance()).isEqualByComparingTo("7000");
    }

    // ── Delete ───────────────────────────────────────────────────

    @Test
    @DisplayName("Delete a transaction removes it from the database")
    void testDelete() {
        Transaction t = service.save(
                new Transaction("Gym", new BigDecimal("999"), "EXPENSE", "Healthcare"));

        assertThat(service.findAll()).hasSize(1);
        service.deleteById(t.getId());
        assertThat(service.findAll()).isEmpty();
    }

    // ── Delete Non-Existent ──────────────────────────────────────

    @Test
    @DisplayName("Deleting a non-existent id does not throw")
    void testDeleteNonExistent() {
        // Should silently ignore; no exception expected
        service.deleteById(999_999L);
    }

    // ── Negative Balance ─────────────────────────────────────────

    @Test
    @DisplayName("Balance can be negative when expenses exceed income")
    void testNegativeBalance() {
        service.save(new Transaction("Salary",   new BigDecimal("1000"),  "INCOME",  "Salary"));
        service.save(new Transaction("Shopping", new BigDecimal("5000"),  "EXPENSE", "Shopping"));

        assertThat(service.balance()).isNegative();
    }

    // ── Empty state ──────────────────────────────────────────────

    @Test
    @DisplayName("Empty database returns zero totals")
    void testEmptyTotals() {
        assertThat(service.totalIncome()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(service.totalExpenses()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(service.balance()).isEqualByComparingTo(BigDecimal.ZERO);
    }

    // ── FindById ─────────────────────────────────────────────────

    @Test
    @DisplayName("findById returns the correct transaction")
    void testFindById() {
        Transaction saved = service.save(
                new Transaction("Investment", new BigDecimal("25000"), "INCOME", "Investment"));

        assertThat(service.findById(saved.getId())).isPresent();
        assertThat(service.findById(saved.getId()).get().getTitle()).isEqualTo("Investment");
    }
}
