package com.financetracker.controller;

import com.financetracker.model.Transaction;
import com.financetracker.service.TransactionService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * MockMvc tests for TransactionController — verifies HTTP layer behaviour
 * without starting a full server.
 */
@WebMvcTest(TransactionController.class)
class TransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TransactionService service;

    // ── GET / ────────────────────────────────────────────────────

    @Test
    @DisplayName("GET / returns 200 and renders index view")
    void dashboardReturns200() throws Exception {
        Mockito.when(service.findAll()).thenReturn(Collections.emptyList());
        Mockito.when(service.totalIncome()).thenReturn(BigDecimal.ZERO);
        Mockito.when(service.totalExpenses()).thenReturn(BigDecimal.ZERO);
        Mockito.when(service.balance()).thenReturn(BigDecimal.ZERO);

        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model().attributeExists("transactions"))
                .andExpect(model().attributeExists("totalIncome"))
                .andExpect(model().attributeExists("totalExpenses"))
                .andExpect(model().attributeExists("balance"));
    }

    @Test
    @DisplayName("GET / populates model with transaction list")
    void dashboardPopulatesTransactions() throws Exception {
        Transaction t = new Transaction("Salary", new BigDecimal("5000"), "INCOME", "Salary");
        Mockito.when(service.findAll()).thenReturn(List.of(t));
        Mockito.when(service.totalIncome()).thenReturn(new BigDecimal("5000"));
        Mockito.when(service.totalExpenses()).thenReturn(BigDecimal.ZERO);
        Mockito.when(service.balance()).thenReturn(new BigDecimal("5000"));

        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("transactions", hasSize(1)));
    }

    // ── POST /transactions ───────────────────────────────────────

    @Test
    @DisplayName("POST /transactions with valid data redirects to /")
    void addTransactionRedirects() throws Exception {
        Transaction saved = new Transaction("Salary", new BigDecimal("10000"), "INCOME", "Salary");
        Mockito.when(service.save(Mockito.any())).thenReturn(saved);

        mockMvc.perform(post("/transactions")
                        .param("title",    "Salary")
                        .param("amount",   "10000")
                        .param("type",     "INCOME")
                        .param("category", "Salary"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));
    }

    @Test
    @DisplayName("POST /transactions with missing title returns 200 with validation errors")
    void addTransactionValidationFails() throws Exception {
        Mockito.when(service.findAll()).thenReturn(Collections.emptyList());
        Mockito.when(service.totalIncome()).thenReturn(BigDecimal.ZERO);
        Mockito.when(service.totalExpenses()).thenReturn(BigDecimal.ZERO);
        Mockito.when(service.balance()).thenReturn(BigDecimal.ZERO);

        mockMvc.perform(post("/transactions")
                        .param("title",    "")   // blank — should fail
                        .param("amount",   "100")
                        .param("type",     "INCOME")
                        .param("category", "Salary"))
                .andExpect(status().isOk())          // re-renders form
                .andExpect(view().name("index"))
                .andExpect(model().hasErrors());
    }

    // ── GET /health ──────────────────────────────────────────────

    @Test
    @DisplayName("GET /health returns 200 with status UP")
    void healthEndpoint() throws Exception {
        mockMvc.perform(get("/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"));
    }

    // ── POST /transactions/{id}/delete ───────────────────────────

    @Test
    @DisplayName("POST /transactions/{id}/delete redirects to /")
    void deleteTransactionRedirects() throws Exception {
        Mockito.doNothing().when(service).deleteById(1L);

        mockMvc.perform(post("/transactions/1/delete"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));
    }
}
