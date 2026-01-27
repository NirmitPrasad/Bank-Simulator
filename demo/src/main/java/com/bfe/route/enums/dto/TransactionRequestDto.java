package com.bfe.route.enums.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

public class TransactionRequestDto {

    @NotNull(message = "Transaction type is required")
    private String type;  // DEPOSIT or WITHDRAW

    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be positive")
    private BigDecimal amount;

    private String description;
    
    // Use account number based operations
    @NotBlank(message = "Account number is required")
    private String accountNumber;
    
    // For transfer operations
    private String receiverBy;
    private String modeOfTransaction;
    
    // MPIN for transaction validation
    private String mpin;

    // Getters and Setters
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getReceiverBy() {
        return receiverBy;
    }

    public void setReceiverBy(String receiverBy) {
        this.receiverBy = receiverBy;
    }

    public String getModeOfTransaction() {
        return modeOfTransaction;
    }

    public void setModeOfTransaction(String modeOfTransaction) {
        this.modeOfTransaction = modeOfTransaction;
    }

    public String getMpin() {
        return mpin;
    }

    public void setMpin(String mpin) {
        this.mpin = mpin;
    }
}
