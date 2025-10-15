package com.bfe.route.enums.services;

import com.bfe.route.enums.entity.Account;
import com.bfe.route.enums.entity.Transaction;
import com.bfe.route.enums.exceptions.ResourceNotFoundException;
import com.bfe.route.enums.exceptions.InsufficientBalanceException;
import com.bfe.route.enums.exceptions.TransactionFailedException;
import com.bfe.route.repository.AccountDetailsRepository;
import com.bfe.route.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import jakarta.mail.MessagingException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class TransactionService {
    private static final Logger logger = LoggerFactory.getLogger(TransactionService.class);

    private final TransactionRepository transactionRepository;
    private final AccountDetailsRepository accountDetailsRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    public TransactionService(TransactionRepository transactionRepository,
                              AccountDetailsRepository accountDetailsRepository) {
        this.transactionRepository = transactionRepository;
        this.accountDetailsRepository = accountDetailsRepository;
    }

    @Transactional
    public Transaction transfer(Long fromAccountId, Long toAccountId, BigDecimal amount,
                             String description, String receivedBy, String senderAccount) {
        try {
            Account fromAccount = accountDetailsRepository.findById(fromAccountId)
                    .orElseThrow(() -> new ResourceNotFoundException("Sender account not found: " + fromAccountId));
            Account toAccount = accountDetailsRepository.findById(toAccountId)
                    .orElseThrow(() -> new ResourceNotFoundException("Receiver account not found: " + toAccountId));

            logger.debug("Initiating transfer: {} -> {} amount: {}", fromAccountId, toAccountId, amount);

            // Validate amount
            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException("Transfer amount must be positive");
            }

            // Check balance
            if (fromAccount.getBalance().compareTo(amount) < 0) {
                throw new InsufficientBalanceException("Insufficient balance in account: " + fromAccountId);
            }

            // Update balances
            BigDecimal fromNewBalance = fromAccount.getBalance().subtract(amount);
            BigDecimal toNewBalance = toAccount.getBalance().add(amount);

            accountDetailsRepository.updateBalance(fromAccountId, fromNewBalance);
            accountDetailsRepository.updateBalance(toAccountId, toNewBalance);

            // Create transactions
            Transaction debitTx = createTransactionRecord(fromAccount, amount, fromNewBalance, 
                "TRANSFER_DEBIT", description, toAccountId.toString(), senderAccount);
            Transaction creditTx = createTransactionRecord(toAccount, amount, toNewBalance,
                "TRANSFER_CREDIT", description, fromAccountId.toString(), senderAccount);

            // Send notifications
            sendTransactionNotifications(fromAccount, toAccount, amount, debitTx.getUtrNumber(), 
                fromNewBalance, toNewBalance);

            return debitTx;

        } catch (Exception e) {
            logger.error("Transfer failed: {}", e.getMessage(), e);
            throw new TransactionFailedException("Transfer failed: " + e.getMessage(), e);
        }
    }

    private Transaction createTransactionRecord(Account account, BigDecimal amount, 
            BigDecimal newBalance, String type, String description, String receivedBy, 
            String senderAccount) {
        Transaction tx = new Transaction();
        tx.setAccount(account);
        tx.setTransactionAmount(amount);
        tx.setBalanceAmount(newBalance);
        tx.setTransactionDate(LocalDateTime.now());
        tx.setDebitedDate(LocalDateTime.now());
        tx.setTransactionType(type);
        tx.setDescription(description != null ? description : 
            String.format("%s with account %s", type, receivedBy));
        tx.setReceiverBy(receivedBy);
        tx.setModeOfTransaction(senderAccount != null ? senderAccount : "TRANSFER");
        tx.setModifiedBy("System");
        tx.setUtrNumber(generateUtr());
        return transactionRepository.save(tx);
    }

    private void sendTransactionNotifications(Account fromAccount, Account toAccount, 
            BigDecimal amount, String utrRef, BigDecimal fromNewBalance, 
            BigDecimal toNewBalance) {
        try {
            String dateTime = LocalDateTime.now().toString();

            if (fromAccount.getEmail() != null) {
                emailService.sendTransactionEmail(
                    fromAccount.getEmail(),
                    fromAccount.getAccountHolderName(),
                    "DEBIT",
                    fromAccount.getAccountNumber().substring(fromAccount.getAccountNumber().length() - 4),
                    amount.toString(),
                    utrRef,
                    fromNewBalance.toString(),
                    dateTime
                );
            }

            if (toAccount.getEmail() != null) {
                emailService.sendTransactionEmail(
                    toAccount.getEmail(),
                    toAccount.getAccountHolderName(),
                    "CREDIT",
                    toAccount.getAccountNumber().substring(toAccount.getAccountNumber().length() - 4),
                    amount.toString(),
                    utrRef,
                    toNewBalance.toString(),
                    dateTime
                );
            }
        } catch (MessagingException e) {
            logger.error("Failed to send transaction notification: {}", e.getMessage(), e);
        }
    }

    @Transactional
    public Transaction deposit(Long accountId, BigDecimal amount) {
        Account account = accountDetailsRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found"));

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }

        BigDecimal newBalance = account.getBalance().add(amount);
        accountDetailsRepository.updateBalance(accountId, newBalance);

        Transaction transaction = new Transaction();
        transaction.setAccount(account);
        transaction.setTransactionAmount(amount);
        transaction.setBalanceAmount(newBalance);
        transaction.setTransactionDate(LocalDateTime.now());
        transaction.setTransactionType("DEPOSIT");
        transaction.setDescription("Deposit transaction");
        transaction.setModifiedBy("System");
        transaction.setUtrNumber(generateUtr());
        
        Transaction savedTransaction = transactionRepository.save(transaction);

        // Send email notification
        try {
            if (account.getEmail() != null) {
                emailService.sendTransactionEmail(
                    account.getEmail(),
                    account.getAccountHolderName(),
                    "CREDIT",
                    account.getAccountNumber().substring(account.getAccountNumber().length() - 4),
                    amount.toString(),
                    savedTransaction.getUtrNumber(),
                    newBalance.toString(),
                    LocalDateTime.now().toString()
                );
            }
        } catch (MessagingException e) {
            System.err.println(" Email sending failed: " + e.getMessage());
        }

        return savedTransaction;
    }

    @Transactional
    public Transaction withdraw(Long accountId, BigDecimal amount) {
        Account account = accountDetailsRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found"));

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }

        if (account.getBalance().compareTo(amount) < 0) {
            throw new IllegalStateException("Insufficient balance for withdrawal");
        }

        BigDecimal newBalance = account.getBalance().subtract(amount);
        accountDetailsRepository.updateBalance(accountId, newBalance);

        Transaction transaction = new Transaction();
        transaction.setAccount(account);
        transaction.setTransactionAmount(amount);
        transaction.setBalanceAmount(newBalance);
        transaction.setTransactionDate(LocalDateTime.now());
        transaction.setTransactionType("WITHDRAWAL");
        transaction.setDescription("Withdrawal transaction");
        transaction.setModifiedBy("System");
        transaction.setUtrNumber(generateUtr());
        
        Transaction savedTransaction = transactionRepository.save(transaction);

        // Send email notification
        try {
            if (account.getEmail() != null) {
                emailService.sendTransactionEmail(
                    account.getEmail(),
                    account.getAccountHolderName(),
                    "DEBIT",
                    account.getAccountNumber().substring(account.getAccountNumber().length() - 4),
                    amount.toString(),
                    savedTransaction.getUtrNumber(),
                    newBalance.toString(),
                    LocalDateTime.now().toString()
                );
            }
        } catch (MessagingException e) {
            System.err.println(" Email sending failed: " + e.getMessage());
        }

        return savedTransaction;
    }

    private String generateUtr() {
        String uuidNoDash = UUID.randomUUID().toString().replace("-", "");
        return "UTR" + uuidNoDash.substring(0, 29);
    }
}
