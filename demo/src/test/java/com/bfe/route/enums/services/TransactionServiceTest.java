// package com.bfe.route.enums.services;

// import com.bfe.route.enums.entity.Account;
// import com.bfe.route.enums.entity.Transaction;
// import com.bfe.route.repository.AccountDetailsRepository;
// import com.bfe.route.repository.TransactionRepository;
// import org.junit.jupiter.api.Test;
// import org.junit.jupiter.api.extension.ExtendWith;
// import org.mockito.InjectMocks;
// import org.mockito.Mock;
// import org.mockito.junit.jupiter.MockitoExtension;

// import java.math.BigDecimal;
// import java.time.LocalDate;
// import java.util.Optional;

// import static org.junit.jupiter.api.Assertions.*;
// import static org.mockito.ArgumentMatchers.any;
// import static org.mockito.Mockito.verify;
// import static org.mockito.Mockito.when;

// @ExtendWith(MockitoExtension.class)
// class TransactionServiceTest {

//     @Mock
//     private AccountDetailsRepository accountDetailsRepository;

//     @Mock
//     private TransactionRepository transactionRepository;

//     @InjectMocks
//     private TransactionService transactionService;

//     @Test
//     void deposit_existingAccount_savesTransactionAndReturnsIt() {
//         // Arrange
//         Long accountId = 4L;
//         Account account = new Account();
//         account.setAccountId(accountId);  // Changed from accountId.intValue()
//         account.setBalance(new BigDecimal("1000.00"));

//         when(accountDetailsRepository.findById(accountId))
//                 .thenReturn(Optional.of(account));

//         Transaction savedTx = new Transaction();
//         savedTx.setTransactionId(99L);
//         savedTx.setAccountId(accountId);
//         savedTx.setTransactionAmount(new BigDecimal("500.00"));
//         savedTx.setTransactionType("DEPOSIT");
//         savedTx.setTransactionDate(LocalDate.now());
//         savedTx.setBalanceAmount(new BigDecimal("1500.00"));
//         savedTx.setModeOfTransaction("CREDIT");
//         savedTx.setDescription("Test deposit");
//         savedTx.setUtrNumber("UTR" + System.currentTimeMillis());

//         when(transactionRepository.save(any(Transaction.class)))
//                 .thenReturn(savedTx);

//         // Act
//         Transaction result = transactionService.deposit(accountId, new BigDecimal("500.00"));

//         // Assert
//         assertNotNull(result);
//         assertEquals(99L, result.getTransactionId());
//         assertEquals(new BigDecimal("500.00"), result.getTransactionAmount());
//         assertEquals("DEPOSIT", result.getTransactionType());
//         verify(accountDetailsRepository).findById(accountId);
//         verify(accountDetailsRepository).save(any(Account.class));
//         verify(transactionRepository).save(any(Transaction.class));
//     }
// }
