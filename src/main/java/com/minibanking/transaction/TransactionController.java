package com.minibanking.transaction;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    /**
     * POST /api/transactions/transfer
     * Expects JSON like: {"fromAccountId": 1, "toAccountId": 2, "amount": 50.00}
     */
    @PostMapping("/transfer")
    public ResponseEntity<Transaction> transfer(@RequestBody Map<String, Object> request) {
        Long fromAccountId = parseLong(request.get("fromAccountId"));
        Long toAccountId = parseLong(request.get("toAccountId"));
        BigDecimal amount = parseBigDecimal(request.get("amount"));

        if (fromAccountId == null || toAccountId == null || amount == null) {
            throw new IllegalArgumentException("fromAccountId, toAccountId, and amount are required");
        }

        Transaction transaction = transactionService.transfer(fromAccountId, toAccountId, amount);
        return ResponseEntity.ok(transaction);
    }

    /**
     * GET /api/transactions/account/{accountId}
     */
    @GetMapping("/account/{accountId}")
    public ResponseEntity<List<Transaction>> getTransactionHistory(@PathVariable Long accountId) {
        return ResponseEntity.ok(transactionService.getTransactionHistory(accountId));
    }
    
    @GetMapping
    public ResponseEntity<List<Transaction>> getAllTransactions() {
        return ResponseEntity.ok(transactionService.getAllTransactions());
    }

    // Helper methods to safely parse JSON numbers
    private Long parseLong(Object value) {
        if (value instanceof Number) {
            return ((Number) value).longValue();
        } else if (value instanceof String) {
            return Long.parseLong((String) value);
        }
        return null;
    }

    private BigDecimal parseBigDecimal(Object value) {
        if (value instanceof Number) {
            return new BigDecimal(value.toString());
        } else if (value instanceof String) {
            return new BigDecimal((String) value);
        }
        return null;
    }
}
