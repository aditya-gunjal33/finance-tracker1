package com.financetracker.controller;

import com.financetracker.model.Transaction;
import com.financetracker.service.TransactionService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Map;

@Controller
public class TransactionController {

    private final TransactionService service;

    public TransactionController(TransactionService service) {
        this.service = service;
    }

    // ── Dashboard ─────────────────────────────────────────────────

    @GetMapping("/")
    public String dashboard(Model model) {
        model.addAttribute("transactions", service.findAll());
        model.addAttribute("totalIncome",   service.totalIncome());
        model.addAttribute("totalExpenses", service.totalExpenses());
        model.addAttribute("balance",       service.balance());
        model.addAttribute("transaction",   new Transaction());  // blank form object
        return "index";
    }

    // ── Add Transaction ───────────────────────────────────────────

    @PostMapping("/transactions")
    public String addTransaction(@Valid @ModelAttribute("transaction") Transaction transaction,
                                 BindingResult result,
                                 Model model,
                                 RedirectAttributes redirectAttrs) {

        if (result.hasErrors()) {
            // Re-render the page with validation errors
            model.addAttribute("transactions", service.findAll());
            model.addAttribute("totalIncome",   service.totalIncome());
            model.addAttribute("totalExpenses", service.totalExpenses());
            model.addAttribute("balance",       service.balance());
            return "index";
        }

        // Normalise type to uppercase
        transaction.setType(transaction.getType().toUpperCase());
        service.save(transaction);
        redirectAttrs.addFlashAttribute("successMessage", "Transaction added successfully!");
        return "redirect:/";
    }

    // ── Delete Transaction ────────────────────────────────────────

    @PostMapping("/transactions/{id}/delete")
    public String deleteTransaction(@PathVariable Long id,
                                    RedirectAttributes redirectAttrs) {
        service.deleteById(id);
        redirectAttrs.addFlashAttribute("successMessage", "Transaction deleted.");
        return "redirect:/";
    }

    // ── Health Endpoint ───────────────────────────────────────────

    @GetMapping("/health")
    @ResponseBody
    public ResponseEntity<Map<String, String>> health() {
        return ResponseEntity.ok(Map.of("status", "UP"));
    }
}
