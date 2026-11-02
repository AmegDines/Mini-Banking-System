package com.minibanking.account;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import com.minibanking.exception.AccountNotFoundException;
import com.minibanking.exception.InsufficientBalanceException;

/**
 * AccountService handles the business logic.
 * @Service tells Spring this is a service component (a type of @Component).
 */
@Service
public class AccountService {

    private final AccountRepository accountRepository;

    /**
     * Constructor injection: Spring automatically provides the AccountRepository when creating this service.
     */
    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public Account createAccount(Account account) {
        if (account.getBalance() == null || account.getBalance().signum() < 0) throw new IllegalArgumentException("Initial balance cannot be negative");
        if (account.getAccountType() == null || account.getAccountType().isBlank()) account.setAccountType("CHECKING");
        if (account.getAccountNumber() == null) account.setAccountNumber("MB" + UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase());
        return accountRepository.save(account);
    }

    public Account getByAccountNumber(String accountNumber) {
        return accountRepository.findByAccountNumber(accountNumber)
            .orElseThrow(() -> new AccountNotFoundException("Account not found: " + accountNumber));
    }

    public Account getAccount(Long id) {
        return accountRepository.findById(id)
                .orElseThrow(() -> new AccountNotFoundException("Account not found with ID: " + id));
    }

    public List<Account> getAllAccounts() {
        return accountRepository.findAll();
    }

    /**
     * @Transactional ensures that the deposit operation happens within a database transaction.
     * If an error occurs, the changes will be rolled back.
     */
    @Transactional
    public Account deposit(Long accountId, BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Deposit amount must be positive");
        }

        Account account = getAccount(accountId);
        account.setBalance(account.getBalance().add(amount));
        return accountRepository.save(account);
    }

    @Transactional
    public Account withdraw(Long accountId, BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Withdrawal amount must be positive");
        }

        Account account = getAccount(accountId);
        if (account.getBalance().compareTo(amount) < 0) {
            throw new InsufficientBalanceException("Insufficient funds for withdrawal");
        }

        account.setBalance(account.getBalance().subtract(amount));
        return accountRepository.save(account);
    }

    @Transactional
    public Account depositByNumber(String accountNumber, BigDecimal amount) {
        validateAmount(amount, "Deposit");
        Account account = getByAccountNumber(accountNumber);
        account.setBalance(account.getBalance().add(amount));
        return accountRepository.save(account);
    }

    @Transactional
    public Account withdrawByNumber(String accountNumber, BigDecimal amount) {
        validateAmount(amount, "Withdrawal");
        Account account = getByAccountNumber(accountNumber);
        if (account.getBalance().compareTo(amount) < 0) throw new InsufficientBalanceException("Insufficient balance for this withdrawal");
        account.setBalance(account.getBalance().subtract(amount));
        return accountRepository.save(account);
    }

    private void validateAmount(BigDecimal amount, String operation) {
        if (amount == null || amount.signum() <= 0) throw new IllegalArgumentException(operation + " amount must be positive");
    }
}
