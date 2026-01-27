package com.bfe.route.enums.models;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "account_details")
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "customer_id")
    private Integer customerId;

    @Column(name = "account_type")
    private String accountType;

    @Column(name = "bank_name")
    private String bankName;

    @Column(name = "branch")
    private String branch;

    @Column(name = "balance")
    private BigDecimal balance;

    @Column(name = "account_number")
    private String accountNumber;

    @Column(name = "ifsc_code")
    private String ifscCode;

    @Column(name = "name_on_account")
    private String nameOnAccount;

    @Column(name = "phone_linked")
    private String phoneLinked;

    @Column(name = "saving_amount")
    private BigDecimal savingAmount;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private AccountStatus status;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "modified_at")
    private LocalDateTime modifiedAt;

    // Getters
    public Integer getId() {
        return id;
    }

    public Integer getAccountId() {
        return id;  
    }

    public Integer getCustomerId() { return customerId; }
    public String getAccountType() { return accountType; }
    public String getBankName() { return bankName; }
    public String getBranch() { return branch; }
    public BigDecimal getBalance() { return balance; }
    public String getAccountNumber() { return accountNumber; }
    public String getIfscCode() { return ifscCode; }
    public String getNameOnAccount() { return nameOnAccount; }
    public String getPhoneLinked() { return phoneLinked; }
    public BigDecimal getSavingAmount() { return savingAmount; }
    public AccountStatus getStatus() { return status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getModifiedAt() { return modifiedAt; }

    // Setters
    public void setId(Integer id) {
        this.id = id;
    }

    public void setAccountId(Integer accountId) {
        this.id = accountId;  
    }

    public void setCustomerId(Integer customerId) { this.customerId = customerId; }
    public void setAccountType(String accountType) { this.accountType = accountType; }
    public void setBankName(String bankName) { this.bankName = bankName; }
    public void setBranch(String branch) { this.branch = branch; }
    public void setBalance(BigDecimal balance) { this.balance = balance; }
    public void setAccountNumber(String accountNumber) { this.accountNumber = accountNumber; }
    public void setIfscCode(String ifscCode) { this.ifscCode = ifscCode; }
    public void setNameOnAccount(String nameOnAccount) { this.nameOnAccount = nameOnAccount; }
    public void setPhoneLinked(String phoneLinked) { this.phoneLinked = phoneLinked; }
    public void setSavingAmount(BigDecimal savingAmount) { this.savingAmount = savingAmount; }
    public void setStatus(AccountStatus status) { this.status = status; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public void setModifiedAt(LocalDateTime modifiedAt) { this.modifiedAt = modifiedAt; }

    public void setAmount(BigDecimal amount) { 
        this.balance = amount; 
    }
    
    public BigDecimal getAmount() { 
        return this.balance; 
    }
}

