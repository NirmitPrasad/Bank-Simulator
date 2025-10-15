package com.bfe.route.enums.models;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "transaction_details")
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "transaction_id")
    private Long id;

    @Column(name = "utr_number")
    private String utrNumber;

    @Column(name = "transaction_date")
    private LocalDate transactionDate;

    @Column(name = "transaction_amount")
    private BigDecimal amount;  

    @Column(name = "debited_date")
    private LocalDate debitedDate;

    @Column(name = "account_id")
    private Integer accountId;

    @Column(name = "balance_amount")
    private BigDecimal balanceAmount;

    @Column(name = "description")
    private String description;

    @Column(name = "modified_by")
    private String modifiedBy;

    @Column(name = "receiver_by")
    private String receiverBy;

    @Enumerated(EnumType.STRING)
    @Column(name = "transaction_type")
    private TransactionType type;  

    @Column(name = "mode_of_transaction")
    private String modeOfTransaction;

    @ManyToOne
    @JoinColumn(name = "account_id", insertable = false, updatable = false)
    private Account account;

    @Column(name = "created_at")
    private LocalDateTime createdAt;


    // Constructors
    

    public Transaction() {
    }

    public Transaction(Long id, Integer accountId, BigDecimal amount,
                       LocalDate transactionDate, TransactionType type, String description) {
        this.id = id;
        this.accountId = accountId;
        this.amount = amount;
        this.transactionDate = transactionDate;
        this.type = type;
        this.description = description;
    }

    
    // Getters and Setters

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUtrNumber() { return utrNumber; }
    public void setUtrNumber(String utrNumber) { this.utrNumber = utrNumber; }

    public LocalDate getTransactionDate() { return transactionDate; }
    public void setTransactionDate(LocalDate transactionDate) { this.transactionDate = transactionDate; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public LocalDate getDebitedDate() { return debitedDate; }
    public void setDebitedDate(LocalDate debitedDate) { this.debitedDate = debitedDate; }

    public Integer getAccountId() { return accountId; }
    public void setAccountId(Integer accountId) { this.accountId = accountId; }

    public BigDecimal getBalanceAmount() { return balanceAmount; }
    public void setBalanceAmount(BigDecimal balanceAmount) { this.balanceAmount = balanceAmount; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getModifiedBy() { return modifiedBy; }
    public void setModifiedBy(String modifiedBy) { this.modifiedBy = modifiedBy; }

    public String getReceiverBy() { return receiverBy; }
    public void setReceiverBy(String receiverBy) { this.receiverBy = receiverBy; }

    public TransactionType getType() { return type; }
    public void setType(TransactionType type) { this.type = type; }

    public String getModeOfTransaction() { return modeOfTransaction; }
    public void setModeOfTransaction(String modeOfTransaction) { this.modeOfTransaction = modeOfTransaction; }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
        if (account != null) {
            this.accountId = account.getId();
        }
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }


    public BigDecimal getTransactionAmount() {
        return this.amount;
    }

    public TransactionType getTransactionType() {
        return this.type;
    }
}
