package com.bfe.route.enums.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public class AccountUpdateDto {
    @Pattern(regexp = "^[A-Za-z0-9]{5,20}$", message = "Account number must be 5-20 alphanumeric characters")
    private String accountNumber;

    @Pattern(regexp = "^[A-Za-z\\s]{2,50}$", message = "Name must be 2-50 characters long and contain only letters")
    private String nameOnAccount;

    @Pattern(regexp = "^[0-9]{10}$", message = "Phone number must be exactly 10 digits")
    private String phoneLinked;

    @Pattern(regexp = "^[A-Z]{4}0[A-Z0-9]{6}$", message = "Invalid IFSC code format")
    private String ifscCode;

    @DecimalMin(value = "0.0", inclusive = true, message = "Balance cannot be negative")
    private BigDecimal balance;

    @Pattern(regexp = "^(SAVINGS|CURRENT)$", message = "Account type must be 'SAVINGS' or 'CURRENT'")
    private String accountType;

    @Size(min = 2, max = 100, message = "Bank name must be between 2 and 100 characters")
    private String bankName;

    @Size(min = 2, max = 100, message = "Branch must be between 2 and 100 characters")
    private String branch;

    @Pattern(regexp = "^(ACTIVE|INACTIVE|FROZEN)$", message = "Status must be ACTIVE, INACTIVE or FROZEN")
    private String status;

    @DecimalMin(value = "0.0", inclusive = true, message = "Saving amount cannot be negative")
    private BigDecimal savingAmount;

    // Getters
    public String getAccountNumber() { return accountNumber; }
    public String getNameOnAccount() { return nameOnAccount; }
    public String getPhoneLinked() { return phoneLinked; }
    public String getIfscCode() { return ifscCode; }
    public BigDecimal getBalance() { return balance; }
    public String getAccountType() { return accountType; }
    public String getBankName() { return bankName; }
    public String getBranch() { return branch; }
    public String getStatus() { return status; }
    public BigDecimal getSavingAmount() { return savingAmount; }

    // Setters
    public void setAccountNumber(String accountNumber) { this.accountNumber = accountNumber; }
    public void setNameOnAccount(String nameOnAccount) { this.nameOnAccount = nameOnAccount; }
    public void setPhoneLinked(String phoneLinked) { this.phoneLinked = phoneLinked; }
    public void setIfscCode(String ifscCode) { this.ifscCode = ifscCode; }
    public void setBalance(BigDecimal balance) { this.balance = balance; }
    public void setAccountType(String accountType) { this.accountType = accountType; }
    public void setBankName(String bankName) { this.bankName = bankName; }
    public void setBranch(String branch) { this.branch = branch; }
    public void setStatus(String status) { this.status = status; }
    public void setSavingAmount(BigDecimal savingAmount) { this.savingAmount = savingAmount; }
}
