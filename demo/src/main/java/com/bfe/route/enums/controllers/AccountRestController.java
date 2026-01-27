package com.bfe.route.enums.controllers;

import com.bfe.route.enums.entity.Account;
import com.bfe.route.enums.entity.Transaction;
import com.bfe.route.repository.TransactionRepository;
import com.bfe.route.enums.services.AccountService;
import com.bfe.route.enums.dto.AccountUpdateDto;
import com.bfe.route.enums.dto.TransactionDto;
import com.bfe.route.enums.services.AccountAlreadyExistsException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import jakarta.servlet.http.HttpServletResponse;

// Add import for TransactionRequestDto
import com.bfe.route.enums.dto.TransactionRequestDto;
import com.bfe.route.repository.AccountDetailsRepository;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/accounts")
public class AccountRestController {

    private static final Logger logger = LoggerFactory.getLogger(AccountRestController.class);
    
    private final AccountService accountService;
    private final TransactionRepository transactionRepository;
    private final AccountDetailsRepository accountRepository;
    
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    public AccountRestController(AccountService accountService,
                                 TransactionRepository transactionRepository,
                                 AccountDetailsRepository accountRepository) {
        this.accountService = accountService;
        this.transactionRepository = transactionRepository;
        this.accountRepository = accountRepository;
        this.objectMapper = new ObjectMapper();
        this.objectMapper.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
    }

    // Get all transactions for account by account number
    @GetMapping("/{accountNumber}/transactions")
    public ResponseEntity<List<TransactionDto>> getTransactions(@PathVariable String accountNumber) {
        try {
            List<Transaction> transactions = 
                transactionRepository.findByAccount_AccountNumberOrderByTransactionDateDesc(accountNumber);

            List<TransactionDto> txList = transactions.stream()
                .map(TransactionDto::fromEntity)
                .collect(Collectors.toList());

            return ResponseEntity.ok(txList);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // Get all transactions across all accounts (most recent first)
    @GetMapping("/transactions")
    public ResponseEntity<List<TransactionDto>> getAllTransactions() {
        try {
            List<Transaction> transactions = transactionRepository.findAll(org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.DESC, "transactionDate"));
            List<TransactionDto> txList = transactions.stream()
                .map(TransactionDto::fromEntity)
                .collect(Collectors.toList());
            return ResponseEntity.ok(txList);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // Export transactions to Excel
    @GetMapping("/{accountNumber}/transactions/export")
    public void exportTransactionsExcel(@PathVariable String accountNumber, HttpServletResponse response) {
        try {
            List<Transaction> transactions = 
                transactionRepository.findByAccount_AccountNumberOrderByTransactionDateDesc(accountNumber);

            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("Transactions");

            // Header row
            Row header = sheet.createRow(0);
            String[] cols = new String[]{
                "Transaction ID", "UTR", "Type", "Amount", "Balance After",
                "Date", "Debited Date", "Description", "Receiver", "Mode"
            };
            for (int i = 0; i < cols.length; i++) {
                Cell cell = header.createCell(i);
                cell.setCellValue(cols[i]);
            }

            // Data rows
            int r = 1;
            for (Transaction tx : transactions) {
                Row row = sheet.createRow(r++);
                row.createCell(0).setCellValue(tx.getTransactionId() != null ? tx.getTransactionId() : 0);
                row.createCell(1).setCellValue(tx.getUtrNumber() != null ? tx.getUtrNumber() : "");
                row.createCell(2).setCellValue(tx.getTransactionType() != null ? tx.getTransactionType() : "");
                row.createCell(3).setCellValue(tx.getTransactionAmount() != null ? tx.getTransactionAmount().doubleValue() : 0);
                row.createCell(4).setCellValue(tx.getBalanceAmount() != null ? tx.getBalanceAmount().doubleValue() : 0);
                row.createCell(5).setCellValue(tx.getTransactionDate() != null ? tx.getTransactionDate().toString() : "");
                row.createCell(6).setCellValue(tx.getDebitedDate() != null ? tx.getDebitedDate().toString() : "");
                row.createCell(7).setCellValue(tx.getDescription() != null ? tx.getDescription() : "");
                row.createCell(8).setCellValue(tx.getReceiverBy() != null ? tx.getReceiverBy() : "");
                row.createCell(9).setCellValue(tx.getModeOfTransaction() != null ? tx.getModeOfTransaction() : "");
            }

            for (int i = 0; i < cols.length; i++) sheet.autoSizeColumn(i);

            String filename = "transactions_" + accountNumber + ".xlsx";
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-Disposition", "attachment; filename=" + filename);
            workbook.write(response.getOutputStream());
            workbook.close();
        } catch (Exception e) {
            throw new RuntimeException("Failed to export transactions: " + e.getMessage(), e);
        }
    }

    // Create new account
    @PostMapping("/create")
    public ResponseEntity<?> createAccount(@RequestBody Map<String, Object> request) {
        try {
            Account account = new Account();

            // Optional identifiers; will be auto-generated in service if missing
            if (request.get("customerId") != null) {
                account.setCustomerId(Integer.valueOf(request.get("customerId").toString()));
            }
            if (request.get("accountNumber") != null) {
                account.setAccountNumber(request.get("accountNumber").toString());
            }

            // Optional numeric fields
            if (request.get("balance") != null) {
                account.setBalance(new BigDecimal(request.get("balance").toString()));
            }
            if (request.get("savingAmount") != null) {
                account.setSavingAmount(new BigDecimal(request.get("savingAmount").toString()));
            }

            // Optional strings
            if (request.get("accountType") != null) account.setAccountType(request.get("accountType").toString());
            if (request.get("bankName") != null) account.setBankName(request.get("bankName").toString());
            if (request.get("branch") != null) account.setBranch(request.get("branch").toString());
            if (request.get("status") != null) account.setStatus(request.get("status").toString());
            if (request.get("ifscCode") != null) account.setIfscCode(request.get("ifscCode").toString());
            if (request.get("nameOnAccount") != null) account.setNameOnAccount(request.get("nameOnAccount").toString());
            if (request.get("accountHolderName") != null) account.setAccountHolderName(request.get("accountHolderName").toString());
            if (request.get("phoneLinked") != null) account.setPhoneLinked(request.get("phoneLinked").toString());
            if (request.get("mpin") != null) account.setMpin(request.get("mpin").toString());

            // Provide sensible defaults for fields enforced by entity validation
            if (account.getNameOnAccount() == null || account.getNameOnAccount().trim().isEmpty()) {
                account.setNameOnAccount("Test User");
            }
            // Derive account holder name from nameOnAccount if missing
            account.setAccountHolderName(account.getNameOnAccount());
            // Email
            if (request.get("email") != null) {
                account.setEmail(request.get("email").toString());
            }

            Account created = accountService.addAccount(account);
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                "accountId", created.getId(),
                "message", "Account created successfully!"
            ));
        } catch (AccountAlreadyExistsException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", e.getMessage()));
        }
    }

    // Transfer funds between accounts (by account number)
    @PostMapping("/transfer")
    public ResponseEntity<?> transferFunds(@RequestBody Map<String, Object> request) {
        // Support new keys by account number; keep backward-compatible IDs optionally
        boolean hasNumbers = request.containsKey("fromAccountNumber") && request.containsKey("toAccountNumber");
        boolean hasIds = request.containsKey("fromAccountId") && request.containsKey("toAccountId");

        if ((!hasNumbers && !hasIds) || !request.containsKey("amount")) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Provide fromAccountNumber/toAccountNumber (or IDs) and amount"));
        }

        try {
            BigDecimal amount = new BigDecimal(request.get("amount").toString());
            String description = request.containsKey("description")
                    ? request.get("description").toString()
                    : null;
            String mpin = request.containsKey("mpin") ? request.get("mpin").toString() : null;

            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Amount must be greater than zero"));
            }

            // Validate MPIN for transfers
            if (mpin == null || mpin.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "MPIN is required for transfer transactions"));
            }

            String fromAccountNumber = hasNumbers ? request.get("fromAccountNumber").toString() : null;
            if (fromAccountNumber != null && !accountService.validateMpin(fromAccountNumber, mpin)) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Invalid MPIN. Please check and try again."));
            }

            Transaction tx;
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Transfer successful");
            response.put("amount", amount);

            if (hasNumbers) {
                String toAccountNumber = request.get("toAccountNumber").toString();
                tx = accountService.transferFundsByAccountNumber(fromAccountNumber, toAccountNumber, amount, description);
                response.put("fromAccountNumber", fromAccountNumber);
                response.put("toAccountNumber", toAccountNumber);
            } else {
                Long fromAccountId = Long.valueOf(request.get("fromAccountId").toString());
                Long toAccountId = Long.valueOf(request.get("toAccountId").toString());
                tx = accountService.transferFunds(fromAccountId, toAccountId, amount, description);
                response.put("fromAccountId", fromAccountId);
                response.put("toAccountId", toAccountId);
            }

            response.put("utr", tx.getUtrNumber());
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (com.bfe.route.enums.exceptions.ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (com.bfe.route.enums.exceptions.TransactionFailedException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            String message = e.getMessage() != null ? e.getMessage() : "Transfer failed";
            return ResponseEntity.internalServerError().body(Map.of("error", message));
        }
    }

    // Fetch all accounts
    @GetMapping("/all")
    public List<Account> getAllAccounts() {
        return accountService.getAllAccounts();
    }

    // Delete account by account number and IFSC
    @DeleteMapping
    public ResponseEntity<?> deleteByAccountNumberAndIfsc(@RequestParam String accountNumber,
                                                          @RequestParam String ifscCode) {
        try {
            accountService.deleteByAccountNumberAndIfsc(accountNumber, ifscCode);
            return ResponseEntity.ok(Map.of(
                "message", "Account deleted successfully",
                "accountNumber", accountNumber,
                "ifscCode", ifscCode
            ));
        } catch (com.bfe.route.enums.exceptions.ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", e.getMessage()));
        }
    }

    // Delete account using account number and password
    @DeleteMapping(path = "/delete")
    public ResponseEntity<?> deleteByBody(@RequestBody Map<String, Object> body) {
        try {
            Object acc = body.get("accountNumber");
            Object password = body.get("password");
            
            if (acc == null) {
                return ResponseEntity.badRequest().body(Map.of(
                    "error", "Account number is required"
                ));
            }
            
            if (password == null) {
                return ResponseEntity.badRequest().body(Map.of(
                    "error", "Password is required"
                ));
            }

            String accountNumber = acc.toString();
            String passwordStr = password.toString();
            
            // Find the account to validate password
            Optional<Account> accountOpt = accountRepository.findByAccountNumber(accountNumber);
            if (!accountOpt.isPresent()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Account not found"));
            }
            
            Account account = accountOpt.get();
            
            // Verify password (MPIN)
            if (!passwordStr.equals(account.getMpin())) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of(
                    "error", "Incorrect Password Please try again"
                ));
            }
            
            // Check if account has balance
            if (account.getBalance() != null && account.getBalance().compareTo(BigDecimal.ZERO) > 0) {
                return ResponseEntity.badRequest().body(Map.of(
                    "error", "Cannot delete account with remaining balance. Please withdraw all funds first."
                ));
            }
            
            // Delete all transactions for this account first
            List<Transaction> transactions = transactionRepository.findByAccount_AccountNumberOrderByTransactionDateDesc(accountNumber);
            transactionRepository.deleteAll(transactions);
            
            // Delete the account
            accountRepository.delete(account);
            
            logger.info("Account {} deleted successfully", accountNumber);
            return ResponseEntity.ok(Map.of(
                "message", "Account deleted successfully",
                "accountNumber", accountNumber
            ));
        } catch (Exception e) {
            logger.error("Error deleting account: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Failed to delete account"));
        }
    }

    // Fallback for clients that cannot send DELETE with a body
    @PostMapping(path = "/delete")
    public ResponseEntity<?> deleteByBodyPost(@RequestBody Map<String, Object> body) {
        return deleteByBody(body);
    }

    // Update account details
    @PutMapping("/{id}")
    public ResponseEntity<?> updateAccount(@PathVariable Long id, @RequestBody Map<String, Object> updates) {
        try {
            // Validate the raw input first
            String validationError = validateRawUpdate(updates);
            if (validationError != null) {
                return ResponseEntity.badRequest().body(Map.of("error", validationError));
            }

            // Convert snake_case JSON to AccountUpdateDto manually 
            AccountUpdateDto dto = createAccountUpdateDto(updates);
            
            // Check for duplicate account number
            String accountNumber = (String) updates.get("account_number");
            if (accountNumber != null && accountService.isAccountNumberTaken(accountNumber, id)) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("error", "Account number already exists"));
            }

            Account updated = accountService.updateAccount(id, dto);
            return ResponseEntity.ok(updated);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", e.getMessage()));
        }
    }

    private String validateRawUpdate(Map<String, Object> updates) {
        // Phone number validation
        Object phoneLinked = updates.get("phone_linked");
        if (phoneLinked != null && !phoneLinked.toString().matches("^[0-9]{10}$")) {
            return "Invalid phone number format";
        }
        
        // Name on account validation
        Object nameOnAccount = updates.get("name_on_account");
        if (nameOnAccount != null && nameOnAccount.toString().trim().isEmpty()) {
            return "Name cannot be empty";
        }
        
        // IFSC code validation
        Object ifscCode = updates.get("ifsc_code");
        if (ifscCode != null && !ifscCode.toString().matches("^[A-Z]{4}0[A-Z0-9]{6}$")) {
            return "Invalid IFSC code format";
        }
        
        // Bank name validation
        Object bankName = updates.get("bank_name");
        if (bankName != null && bankName.toString().trim().isEmpty()) {
            return "Bank name cannot be empty";
        }
        
        // Branch validation
        Object branch = updates.get("branch");
        if (branch != null && branch.toString().trim().isEmpty()) {
            return "Branch cannot be empty";
        }
        
        // Balance validation
        Object balance = updates.get("balance");
        if (balance != null) {
            try {
                BigDecimal balanceValue = new BigDecimal(balance.toString());
                if (balanceValue.compareTo(BigDecimal.ZERO) < 0) {
                    return "Balance cannot be negative";
                }
            } catch (NumberFormatException e) {
                return "Invalid balance format";
            }
        }
        
        // Account type validation
        Object accountType = updates.get("account_type");
        if (accountType != null && 
            !accountType.toString().matches("^(savings|current|SAVINGS|CURRENT)$")) {
            return "Invalid account type. Must be 'savings' or 'current'";
        }
        
        // Status validation
        Object status = updates.get("status");
        if (status != null && 
            !status.toString().matches("^(ACTIVE|INACTIVE|FROZEN|active|inactive|frozen)$")) {
            return "Invalid status. Must be 'ACTIVE', 'INACTIVE', or 'FROZEN'";
        }
        
        // Saving amount validation
        Object savingAmount = updates.get("saving_amount");
        if (savingAmount != null) {
            try {
                BigDecimal savingAmountValue = new BigDecimal(savingAmount.toString());
                if (savingAmountValue.compareTo(BigDecimal.ZERO) < 0) {
                    return "Saving amount cannot be negative";
                }
            } catch (NumberFormatException e) {
                return "Invalid saving amount format";
            }
        }
        
        return null; // No validation errors
    }

    private AccountUpdateDto createAccountUpdateDto(Map<String, Object> updates) {
        AccountUpdateDto dto = new AccountUpdateDto();
        
        if (updates.containsKey("account_number")) {
            dto.setAccountNumber((String) updates.get("account_number"));
        }
        if (updates.containsKey("name_on_account")) {
            dto.setNameOnAccount((String) updates.get("name_on_account"));
        }
        if (updates.containsKey("phone_linked")) {
            dto.setPhoneLinked((String) updates.get("phone_linked"));
        }
        if (updates.containsKey("ifsc_code")) {
            dto.setIfscCode((String) updates.get("ifsc_code"));
        }
        if (updates.containsKey("balance")) {
            Object balance = updates.get("balance");
            if (balance != null) {
                dto.setBalance(new BigDecimal(balance.toString()));
            }
        }
        if (updates.containsKey("account_type")) {
            dto.setAccountType((String) updates.get("account_type"));
        }
        if (updates.containsKey("bank_name")) {
            dto.setBankName((String) updates.get("bank_name"));
        }
        if (updates.containsKey("branch")) {
            dto.setBranch((String) updates.get("branch"));
        }
        if (updates.containsKey("status")) {
            dto.setStatus((String) updates.get("status"));
        }
        if (updates.containsKey("saving_amount")) {
            Object savingAmount = updates.get("saving_amount");
            if (savingAmount != null) {
                dto.setSavingAmount(new BigDecimal(savingAmount.toString()));
            }
        }
        
        return dto;
    }

    // Exception Handlers
    @ExceptionHandler(AccountAlreadyExistsException.class)
    public ResponseEntity<?> handleAccountAlreadyExists(AccountAlreadyExistsException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(Map.of("error", e.getMessage()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> handleIllegalArgument(IllegalArgumentException e) {
        return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(
            MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return ResponseEntity.badRequest().body(errors);
    }

    // Deposit and Withdraw funds
    @PostMapping("/transactions")
    public ResponseEntity<?> depositWithdrawFundsByNumber(@RequestBody TransactionRequestDto request) {
        try {
            // Validate MPIN for WITHDRAW transactions
            if ("WITHDRAW".equalsIgnoreCase(request.getType())) {
                if (request.getMpin() == null || request.getMpin().trim().isEmpty()) {
                    return ResponseEntity.badRequest().body(Map.of("error", "MPIN is required for withdrawal transactions"));
                }
                
                // Validate MPIN
                if (!accountService.validateMpin(request.getAccountNumber(), request.getMpin())) {
                    return ResponseEntity.badRequest().body(Map.of("error", "Invalid MPIN. Please check and try again."));
                }
            }
            
            Transaction tx;
            String message;

            if ("DEPOSIT".equalsIgnoreCase(request.getType())) {
                tx = accountService.depositByAccountNumber(request.getAccountNumber(), request.getAmount(), request.getDescription());
                message = "Deposit successful";
            } else if ("WITHDRAW".equalsIgnoreCase(request.getType())) {
                tx = accountService.withdrawByAccountNumber(request.getAccountNumber(), request.getAmount(), request.getDescription());
                message = "Withdrawal successful";
            } else {
                return ResponseEntity.badRequest().body(Map.of("error", "Invalid transaction type. Use DEPOSIT or WITHDRAW"));
            }

            return ResponseEntity.ok(Map.of(
                    "message", message,
                    "accountNumber", request.getAccountNumber(),
                    "amount", request.getAmount(),
                    "utr", tx.getUtrNumber()
            ));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            Throwable root = e;
            while (root.getCause() != null && root.getCause() != root) {
                root = root.getCause();
            }
            String message = root.getMessage() != null ? root.getMessage() : e.getMessage();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Transaction failed: " + message));
        }
    }

}
