package com.bfe.route.enums.entity;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;

@Entity
@Table(name = "account_details")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "account_id")
    private Long id;

    @Column(name = "customer_id")
    @NotNull(message = "Customer ID cannot be null")
    private Integer customerId;

    @Column(name = "account_number", nullable = false, unique = true)
    @NotBlank(message = "Account number is required")
    @Size(min = 1, max = 20, message = "Account number must be between 1 and 20 characters")
    private String accountNumber;

    @Column(name = "balance", precision = 15, scale = 2)
    @DecimalMin(value = "0.0", inclusive = true, message = "Balance cannot be negative")
    private BigDecimal balance;

    @Column(name = "account_type")
    @NotBlank(message = "Account type cannot be blank")
    private String accountType;

    @Column(name = "bank_name")
    @NotBlank(message = "Bank name is required")
    private String bankName;

    @Column(name = "branch")
    private String branch;

    @Column(name = "status")
    @Pattern(regexp = "ACTIVE|INACTIVE|SUSPENDED", message = "Status must be ACTIVE, INACTIVE, or SUSPENDED")
    private String status;

    @Column(name = "ifsc_code")
    @Size(min = 5, max = 15, message = "IFSC code must be between 5 and 15 characters")
    private String ifscCode;

    @Column(name = "name_on_account")
    @NotBlank(message = "Name on account is required")
    private String nameOnAccount;

    // Full name of account holder
    @Column(name = "account_holder_name")
    @NotBlank(message = "Account holder name is required")
    private String accountHolderName;

    @Column(name = "phone_linked")
    @Pattern(regexp = "^[0-9]{10}$", message = "Phone number must be 10 digits")
    private String phoneLinked;

    // Email of account holder
    @Column(name = "email", nullable = false)
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @Column(name = "saving_amount", precision = 15, scale = 2)
    @DecimalMin(value = "0.0", inclusive = true, message = "Saving amount cannot be negative")
    private BigDecimal savingAmount;

    @Column(name = "mpin", length = 6)
    @Pattern(regexp = "^[0-9]{6}$", message = "MPIN must be exactly 6 digits")
    private String mpin;

    @Column(name = "modified_at")
    private LocalDateTime modifiedAt;

    // Optimistic Locking
    @Version
    private Integer version;

    // Relationship with transactions
    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Transaction> transactions = new ArrayList<>();

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Integer getCustomerId() { return customerId; }
    public void setCustomerId(Integer customerId) { this.customerId = customerId; }

    public String getAccountNumber() { return accountNumber; }
    public void setAccountNumber(String accountNumber) { this.accountNumber = accountNumber; }

    public BigDecimal getBalance() { return balance; }
    public void setBalance(BigDecimal balance) { this.balance = balance; }

    public String getAccountType() { return accountType; }
    public void setAccountType(String accountType) { this.accountType = accountType; }

    public String getBankName() { return bankName; }
    public void setBankName(String bankName) { this.bankName = bankName; }

    public String getBranch() { return branch; }
    public void setBranch(String branch) { this.branch = branch; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getIfscCode() { return ifscCode; }
    public void setIfscCode(String ifscCode) { this.ifscCode = ifscCode; }

    public String getNameOnAccount() { return nameOnAccount; }
    public void setNameOnAccount(String nameOnAccount) { this.nameOnAccount = nameOnAccount; }

    public String getAccountHolderName() { return accountHolderName; }
    public void setAccountHolderName(String accountHolderName) { this.accountHolderName = accountHolderName; }

    public String getPhoneLinked() { return phoneLinked; }
    public void setPhoneLinked(String phoneLinked) { this.phoneLinked = phoneLinked; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public BigDecimal getSavingAmount() { return savingAmount; }
    public void setSavingAmount(BigDecimal savingAmount) { this.savingAmount = savingAmount; }

    public String getMpin() { return mpin; }
    public void setMpin(String mpin) { this.mpin = mpin; }

    public LocalDateTime getModifiedAt() { return modifiedAt; }
    public void setModifiedAt(LocalDateTime modifiedAt) { this.modifiedAt = modifiedAt; }

    public Integer getVersion() { return version; }
    public void setVersion(Integer version) { this.version = version; }

    public List<Transaction> getTransactions() { return transactions; }
    public void setTransactions(List<Transaction> transactions) { this.transactions = transactions; }
}
