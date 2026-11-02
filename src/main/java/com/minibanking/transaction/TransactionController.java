package com.minibanking.transaction;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {
    private final TransactionService transactionService;
    public TransactionController(TransactionService transactionService) { this.transactionService = transactionService; }
    public record AmountRequest(@NotBlank String accountNumber, @NotNull @Positive BigDecimal amount) {}
    public record TransferRequest(@NotBlank String senderAccNo, @NotBlank String receiverAccNo, @NotNull @Positive BigDecimal amount) {}
    @PostMapping("/deposit") public Transaction deposit(@Valid @RequestBody AmountRequest request) { return transactionService.deposit(request.accountNumber(), request.amount()); }
    @PostMapping("/withdraw") public Transaction withdraw(@Valid @RequestBody AmountRequest request) { return transactionService.withdraw(request.accountNumber(), request.amount()); }
    @PostMapping("/transfer") public Transaction transfer(@Valid @RequestBody TransferRequest request) { return transactionService.transfer(request.senderAccNo(), request.receiverAccNo(), request.amount()); }
    @GetMapping("/account/{accountNumber}") public List<Transaction> history(@PathVariable String accountNumber) { return transactionService.getTransactionHistory(accountNumber); }
    @GetMapping public List<Transaction> all() { return transactionService.getAllTransactions(); }
}
