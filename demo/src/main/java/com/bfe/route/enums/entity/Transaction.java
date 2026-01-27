package com.bfe.route.enums.entity;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "transaction_details")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "transaction_id")
    private Long transactionId;

    @Column(name = "utr_number", nullable = false, unique = true, length = 32)
    private String utrNumber;

    @Column(name = "transaction_date", nullable = false)
    private LocalDateTime transactionDate;

    @Column(name = "debited_date")
    private LocalDateTime debitedDate;

    @Column(name = "transaction_amount", nullable = false, precision = 15, scale = 2)
    @DecimalMin(value = "0.01", inclusive = true, message = "Transaction amount must be greater than 0")
    private BigDecimal transactionAmount;

    // Relationship to Account
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    @JsonBackReference
    private Account account;

    @Column(name = "balance_amount", precision = 15, scale = 2)
    private BigDecimal balanceAmount;

    @Size(max = 255, message = "Description cannot exceed 255 characters")
    private String description;

    @Column(name = "modified_by")
    private String modifiedBy;

    @Column(name = "receiver_by")
    private String receiverBy;

    @Column(name = "transaction_type")
    private String transactionType;

    @Column(name = "mode_of_transaction")
    private String modeOfTransaction;


    // Getters and Setters
    public Long getTransactionId() { return transactionId; }
    public void setTransactionId(Long transactionId) { this.transactionId = transactionId; }

    public String getUtrNumber() { return utrNumber; }
    public void setUtrNumber(String utrNumber) { this.utrNumber = utrNumber; }

    public LocalDateTime getTransactionDate() { return transactionDate; }
    public void setTransactionDate(LocalDateTime transactionDate) { this.transactionDate = transactionDate; }

    public LocalDateTime getDebitedDate() { return debitedDate; }
    public void setDebitedDate(LocalDateTime debitedDate) { this.debitedDate = debitedDate; }

    public BigDecimal getTransactionAmount() { return transactionAmount; }
    public void setTransactionAmount(BigDecimal transactionAmount) { this.transactionAmount = transactionAmount; }

    public Account getAccount() { return account; }
    public void setAccount(Account account) { this.account = account; }

    public BigDecimal getBalanceAmount() { return balanceAmount; }
    public void setBalanceAmount(BigDecimal balanceAmount) { this.balanceAmount = balanceAmount; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getModifiedBy() { return modifiedBy; }
    public void setModifiedBy(String modifiedBy) { this.modifiedBy = modifiedBy; }

    public String getReceiverBy() { return receiverBy; }
    public void setReceiverBy(String receiverBy) { this.receiverBy = receiverBy; }

    public String getTransactionType() { return transactionType; }
    public void setTransactionType(String transactionType) { this.transactionType = transactionType; }

    public String getModeOfTransaction() { return modeOfTransaction; }
    public void setModeOfTransaction(String modeOfTransaction) { this.modeOfTransaction = modeOfTransaction; }

    
}
