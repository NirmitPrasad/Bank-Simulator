package com.bfe.route.enums.dto;

import com.bfe.route.enums.entity.Transaction;
import java.math.BigDecimal;
import java.time.LocalDateTime;  // Changed from LocalDate

/**
 * DTO returned to clients describing a transaction.
 * Simple mapper from entity -> DTO included.
 */
public class TransactionDto {

    private Long transactionId;
    private String utrNumber;
    private LocalDateTime transactionDate;  // Changed from LocalDate
    private BigDecimal transactionAmount;
    private BigDecimal balanceAmount;
    private String description;
    private String transactionType;
    private String modeOfTransaction;
    private String receiverBy; // Added missing field

    // --- Getters / Setters ---
    public Long getTransactionId() {
        return transactionId;
    }
    public void setTransactionId(Long transactionId) {
        this.transactionId = transactionId;
    }

    public String getUtrNumber() {
        return utrNumber;
    }
    public void setUtrNumber(String utrNumber) {
        this.utrNumber = utrNumber;
    }

    public LocalDateTime getTransactionDate() {
        return transactionDate;
    }
    public void setTransactionDate(LocalDateTime transactionDate) {  // Changed parameter type
        this.transactionDate = transactionDate;
    }

    public BigDecimal getTransactionAmount() {
        return transactionAmount;
    }
    public void setTransactionAmount(BigDecimal transactionAmount) {
        this.transactionAmount = transactionAmount;
    }

    public BigDecimal getBalanceAmount() {
        return balanceAmount;
    }
    public void setBalanceAmount(BigDecimal balanceAmount) {
        this.balanceAmount = balanceAmount;
    }

    public String getTransactionType() {
        return transactionType;
    }
    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
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

    // --- helper mapper from entity -> dto ---
    public static TransactionDto fromEntity(Transaction transaction) {
        TransactionDto dto = new TransactionDto();
        dto.setTransactionId(transaction.getTransactionId());
        dto.setUtrNumber(transaction.getUtrNumber());
        dto.setTransactionDate(transaction.getTransactionDate());  // Now matches the types
        dto.setTransactionAmount(transaction.getTransactionAmount());
        dto.setBalanceAmount(transaction.getBalanceAmount());
        dto.setDescription(transaction.getDescription());
        dto.setTransactionType(transaction.getTransactionType());
        dto.setModeOfTransaction(transaction.getModeOfTransaction());
        return dto;
    }
}
