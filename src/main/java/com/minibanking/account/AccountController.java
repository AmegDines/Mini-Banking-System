package com.minibanking.account;

import com.minibanking.user.User;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {
    private final AccountService accountService;
    public AccountController(AccountService accountService) { this.accountService = accountService; }
    public record CreateAccountRequest(@NotBlank String name, @Email @NotBlank String email,
        @NotBlank String accountType, @PositiveOrZero BigDecimal initialBalance) {}

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Account createAccount(@Valid @RequestBody CreateAccountRequest request) {
        Account account = new Account(request.name(), request.initialBalance() == null ? BigDecimal.ZERO : request.initialBalance());
        account.setAccountType(request.accountType().trim().toUpperCase());
        account.setUser(new User(request.name(), request.email()));
        return accountService.createAccount(account);
    }
    @GetMapping
    public List<Account> getAllAccounts() { return accountService.getAllAccounts(); }
    @GetMapping("/{accountNumber}")
    public Account getAccount(@PathVariable String accountNumber) { return accountService.getByAccountNumber(accountNumber); }
}
