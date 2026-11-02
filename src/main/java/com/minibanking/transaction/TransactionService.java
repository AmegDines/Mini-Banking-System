package com.minibanking.transaction;

import com.minibanking.account.Account;
import com.minibanking.account.AccountService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.List;
import com.minibanking.exception.InsufficientBalanceException;

@Service
public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final AccountService accountService;
    public TransactionService(TransactionRepository transactionRepository, AccountService accountService) {
        this.transactionRepository = transactionRepository; this.accountService = accountService;
    }
    @Transactional
    public Transaction deposit(String accountNumber, BigDecimal amount) {
        Account account = accountService.depositByNumber(accountNumber, amount);
        return transactionRepository.save(new Transaction(null, account.getAccountNumber(), amount, "DEPOSIT"));
    }
    @Transactional
    public Transaction withdraw(String accountNumber, BigDecimal amount) {
        Account account = accountService.withdrawByNumber(accountNumber, amount);
        return transactionRepository.save(new Transaction(account.getAccountNumber(), null, amount, "WITHDRAWAL"));
    }
    @Transactional
    public Transaction transfer(String from, String to, BigDecimal amount) {
        if (from == null || to == null || from.equals(to)) throw new IllegalArgumentException("Sender and receiver must be different accounts");
        if (amount == null || amount.signum() <= 0) throw new IllegalArgumentException("Transfer amount must be positive");
        Account source = accountService.getByAccountNumber(from);
        if (source.getBalance().compareTo(amount) < 0) throw new InsufficientBalanceException("Insufficient balance for this transfer");
        Account sender = accountService.withdrawByNumber(from, amount);
        Account receiver = accountService.depositByNumber(to, amount);
        return transactionRepository.save(new Transaction(sender.getAccountNumber(), receiver.getAccountNumber(), amount, "TRANSFER"));
    }
    public List<Transaction> getTransactionHistory(String accountNumber) {
        accountService.getByAccountNumber(accountNumber);
        return transactionRepository.findBySenderAccNoOrReceiverAccNoOrderByTimestampDesc(accountNumber, accountNumber);
    }
    public List<Transaction> getAllTransactions() { return transactionRepository.findAll(); }
}
