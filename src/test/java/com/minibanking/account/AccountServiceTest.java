package com.minibanking.account;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * JUnit 5 test class for AccountService.
 * @ExtendWith(MockitoExtension.class) enables Mockito support for mocking dependencies.
 */
@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private AccountService accountService;

    private Account testAccount;

    @BeforeEach
    void setUp() {
        testAccount = new Account();
        testAccount.setId(1L);
        testAccount.setOwnerName("John Doe");
        testAccount.setBalance(new BigDecimal("100.00"));
    }

    @Test
    void testDeposit_Success() {
        // Arrange
        when(accountRepository.findById(1L)).thenReturn(Optional.of(testAccount));
        when(accountRepository.save(any(Account.class))).thenReturn(testAccount);

        // Act
        Account updatedAccount = accountService.deposit(1L, new BigDecimal("50.00"));

        // Assert
        assertEquals(new BigDecimal("150.00"), updatedAccount.getBalance());
        verify(accountRepository, times(1)).save(testAccount);
    }

    @Test
    void testWithdraw_Success() {
        // Arrange
        when(accountRepository.findById(1L)).thenReturn(Optional.of(testAccount));
        when(accountRepository.save(any(Account.class))).thenReturn(testAccount);

        // Act
        Account updatedAccount = accountService.withdraw(1L, new BigDecimal("40.00"));

        // Assert
        assertEquals(new BigDecimal("60.00"), updatedAccount.getBalance());
        verify(accountRepository, times(1)).save(testAccount);
    }

    @Test
    void testWithdraw_InsufficientFunds_ThrowsException() {
        // Arrange
        when(accountRepository.findById(1L)).thenReturn(Optional.of(testAccount));

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            accountService.withdraw(1L, new BigDecimal("150.00")); // Trying to withdraw more than 100
        });

        assertEquals("Insufficient funds for withdrawal", exception.getMessage());
        verify(accountRepository, never()).save(any(Account.class));
    }
}
