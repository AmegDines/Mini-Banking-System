package com.minibanking.transaction;

import com.minibanking.account.AccountService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountService accountService;

    public TransactionService(TransactionRepository transactionRepository, AccountService accountService) {
        this.transactionRepository = transactionRepository;
        this.accountService = accountService;
    }

    /**
     * @Transactional ensures this entire transfer operation acts as a single atomic unit.
     * If the withdrawal succeeds but the deposit fails, the withdrawal is rolled back.
     */
    @Transactional
    public Transaction transfer(Long fromAccountId, Long toAccountId, BigDecimal amount) {
        if (fromAccountId.equals(toAccountId)) {
            throw new IllegalArgumentException("Cannot transfer to the same account");
        }

        // 1. Withdraw from source
        accountService.withdraw(fromAccountId, amount);

        // 2. Deposit to destination
        accountService.deposit(toAccountId, amount);

        // 3. Record transaction history
        Transaction transaction = new Transaction(fromAccountId, toAccountId, amount, "SUCCESS");
        return transactionRepository.save(transaction);
    }

    public List<Transaction> getTransactionHistory(Long accountId) {
        // To ensure the account exists first
        accountService.getAccount(accountId);
        return transactionRepository.findByFromAccountIdOrToAccountIdOrderByTimestampDesc(accountId, accountId);
    }
    
    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }
}
