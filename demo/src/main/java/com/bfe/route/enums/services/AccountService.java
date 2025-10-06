package com.bfe.route.enums.services;

import java.time.LocalDateTime;
import com.bfe.route.enums.entity.Account;
import com.bfe.route.enums.entity.Transaction;
import com.bfe.route.enums.dto.AccountUpdateDto;
import com.bfe.route.enums.exceptions.InsufficientBalanceException;
import com.bfe.route.enums.exceptions.AccountAlreadyExistsException;
import com.bfe.route.enums.exceptions.ResourceNotFoundException;
import com.bfe.route.repository.AccountDetailsRepository;
import com.bfe.route.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

@Service
@Transactional
public class AccountService {

    @Autowired 
    private AccountDetailsRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final TransactionService transactionService;

    @Autowired
    public AccountService(AccountDetailsRepository accountRepository,
                          TransactionRepository transactionRepository,
                          TransactionService transactionService) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
        this.transactionService = transactionService;
    }

    // Fetch account by ID
    public Account getById(Long id) {
        return accountRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found with id: " + id));
    }

    // Create account
    public Account addAccount(Account account) {
        Optional<Account> existing = accountRepository.findByAccountNumber(account.getAccountNumber());
        if (existing.isPresent()) {
            throw new AccountAlreadyExistsException(
                    "Account with number " + account.getAccountNumber() + " already exists");
        }
        account.setBalance(account.getBalance() == null ? BigDecimal.ZERO : account.getBalance());
        account.setStatus("ACTIVE");
        return accountRepository.save(account);
    }

    // Fetch all accounts
    public List<Account> getAllAccounts() {
        return accountRepository.findAll();
    }

    // Update account details
    @Transactional
    public Account updateAccount(Long id, AccountUpdateDto dto) {
        Account account = accountRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Account not found"));

        boolean needsUpdate = false;

        if (dto.getAccountNumber() != null && !dto.getAccountNumber().equals(account.getAccountNumber())) {
            if (accountRepository.existsByAccountNumberAndIdNot(dto.getAccountNumber(), id)) {
                throw new AccountAlreadyExistsException("Account number already exists");
            }
            account.setAccountNumber(dto.getAccountNumber());
            needsUpdate = true;
        }

        if (dto.getPhoneLinked() != null && !dto.getPhoneLinked().equals(account.getPhoneLinked())) {
            if (!dto.getPhoneLinked().matches("^\\d{10}$")) {
                throw new IllegalArgumentException("Invalid phone number format");
            }
        }

        if (dto.getNameOnAccount() != null && !dto.getNameOnAccount().equals(account.getNameOnAccount())) {
            if (dto.getNameOnAccount().trim().isEmpty()) {
                throw new IllegalArgumentException("Name cannot be empty");
            }
        }

        if (dto.getIfscCode() != null && !dto.getIfscCode().equals(account.getIfscCode())) {
            if (!dto.getIfscCode().matches("^[A-Z]{4}0[A-Z0-9]{6}$")) {
                throw new IllegalArgumentException("Invalid IFSC code format");
            }
        }

        if (dto.getBalance() != null && !dto.getBalance().equals(account.getBalance())) {
            if (dto.getBalance().compareTo(BigDecimal.ZERO) < 0) {
                throw new IllegalArgumentException("Balance cannot be negative");
            }
            account.setBalance(dto.getBalance());
            needsUpdate = true;
        }

        if (dto.getAccountType() != null && !dto.getAccountType().equalsIgnoreCase(account.getAccountType())) {
            String normalizedType = dto.getAccountType().toLowerCase();
            if (!normalizedType.equals("savings") && !normalizedType.equals("current")) {
                throw new IllegalArgumentException("Invalid account type. Must be 'savings' or 'current'");
            }
        }

        if (dto.getBankName() != null && !dto.getBankName().equals(account.getBankName())) {
            if (dto.getBankName().trim().isEmpty()) {
                throw new IllegalArgumentException("Bank name cannot be empty");
            }
        }

        if (dto.getBranch() != null && !dto.getBranch().equals(account.getBranch())) {
            if (dto.getBranch().trim().isEmpty()) {
                throw new IllegalArgumentException("Branch cannot be empty");
            }
        }

        if (dto.getStatus() != null && !dto.getStatus().equalsIgnoreCase(account.getStatus())) {
            String normalizedStatus = dto.getStatus().toUpperCase();
            if (!normalizedStatus.equals("ACTIVE") && !normalizedStatus.equals("INACTIVE") && !normalizedStatus.equals("FROZEN")) {
                throw new IllegalArgumentException("Invalid status. Must be 'ACTIVE', 'INACTIVE', or 'FROZEN'");
            }
            account.setStatus(normalizedStatus);
            needsUpdate = true;
        }

        if (dto.getSavingAmount() != null && !dto.getSavingAmount().equals(account.getSavingAmount())) {
            if (dto.getSavingAmount().compareTo(BigDecimal.ZERO) < 0) {
                throw new IllegalArgumentException("Saving amount cannot be negative");
            }
        }

        if (needsUpdate) {
            account = accountRepository.save(account);
            accountRepository.flush();
            account = accountRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found after update"));
        }

        return account;
    }

    // Safer deposit using TransactionService
    @Transactional
    public Transaction deposit(Long accountId, BigDecimal amount, String description) {
        Transaction transaction = transactionService.deposit(accountId, amount);
        transaction.setDescription(description != null ? description : "Deposit of " + amount);
        return transactionRepository.save(transaction);
    }

    // Safer withdraw using TransactionService
    @Transactional
    public Transaction withdraw(Long accountId, BigDecimal amount, String description) {
        Transaction transaction = transactionService.withdraw(accountId, amount);
        transaction.setDescription(description != null ? description : "Withdrawal of " + amount);
        return transactionRepository.save(transaction);
    }

    // Transfer funds between two accounts
    @Transactional
    public Transaction transferFunds(Long fromAccountId, Long toAccountId, BigDecimal amount, String description) {
        return transactionService.transfer(fromAccountId, toAccountId, amount, description, null, null);
    }

    // Transfer funds using account numbers instead of IDs
    @Transactional
    public Transaction transferFundsByAccountNumber(String fromAccountNumber, String toAccountNumber,
                                                    BigDecimal amount, String description) {
        Account fromAccount = accountRepository.findByAccountNumber(fromAccountNumber)
            .orElseThrow(() -> new ResourceNotFoundException("Sender account not found with number: " + fromAccountNumber));
        Account toAccount = accountRepository.findByAccountNumber(toAccountNumber)
            .orElseThrow(() -> new ResourceNotFoundException("Receiver account not found with number: " + toAccountNumber));

        return transactionService.transfer(fromAccount.getId(), toAccount.getId(), amount, description, null, null);
    }

    public Transaction processTransaction(Long accountId, String type, BigDecimal amount, String description) {
        Account account = accountRepository.findById(accountId)
            .orElseThrow(() -> new IllegalArgumentException("Account not found"));

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }

        BigDecimal newBalance;
        switch (type.toUpperCase()) {
            case "DEPOSIT":
                newBalance = account.getBalance().add(amount);
                break;
            case "WITHDRAW":
                if (account.getBalance().compareTo(amount) < 0) {
                    throw new IllegalArgumentException("Insufficient funds");
                }
                newBalance = account.getBalance().subtract(amount);
                break;
            default:
                throw new IllegalArgumentException("Invalid transaction type");
        }

        // Update account balance
        account.setBalance(newBalance);
        accountRepository.save(account);

        // Create transaction record
        Transaction transaction = new Transaction();
        transaction.setAccount(account);
        transaction.setTransactionType(type.toUpperCase());
        transaction.setTransactionAmount(amount);
        transaction.setBalanceAmount(newBalance);
        transaction.setDescription(description != null ? description : 
            (type.equalsIgnoreCase("DEPOSIT") ? "Deposit transaction" : "Withdrawal transaction"));
        transaction.setTransactionDate(LocalDateTime.now());
        transaction.setUtrNumber(generateUTR());
        
        return transactionRepository.save(transaction);
    }

    private String generateUTR() {
        return "UTR" + System.currentTimeMillis();
    }

    private Account findAccount(Long accountId) {
        return accountRepository.findById(accountId)
            .orElseThrow(() -> new IllegalArgumentException("Account not found"));
    }

    public boolean isAccountNumberTaken(String accountNumber, Long excludeId) {
        return accountRepository.findByAccountNumberAndIdNot(accountNumber, excludeId).isPresent();
    }
}
