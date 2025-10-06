package com.bfe.route.enums.controllers;

import com.bfe.route.enums.entity.Account;
import com.bfe.route.enums.dto.AccountUpdateDto;
import com.bfe.route.enums.services.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/accounts")
public class AccountController {

    @Autowired
    private AccountService accountService;

    // Create account
    @PostMapping
    public Account createAccount(@RequestBody Account account) {
        return accountService.addAccount(account);
    }

    // Get all accounts
    @GetMapping
    public List<Account> getAllAccounts() {
        return accountService.getAllAccounts();
    }

    // Get account by ID
    @GetMapping("/{id}")
    public Account getAccountById(@PathVariable Long id) {
        return accountService.getById(id);
    }

    // Update account details
    @PutMapping("/{id}")
    public Account updateAccount(@PathVariable Long id, @RequestBody AccountUpdateDto dto) {
        return accountService.updateAccount(id, dto);
    }

    // Deposit
    @PostMapping("/{id}/deposit")
    public Object deposit(@PathVariable Long id,
                         @RequestParam BigDecimal amount,
                         @RequestParam(required = false) String description) {
        return accountService.deposit(id, amount, 
            description != null ? description : "Deposit transaction");
    }

    // Withdraw
    @PostMapping("/{id}/withdraw")
    public Object withdraw(@PathVariable Long id,
                          @RequestParam BigDecimal amount,
                          @RequestParam(required = false) String description) {
        return accountService.withdraw(id, amount, 
            description != null ? description : "Withdrawal transaction");
    }

    // Transfer between accounts
    @PostMapping("/transfer")
    public Object transferFunds(@RequestParam Long fromAccountId,
                                @RequestParam Long toAccountId,
                                @RequestParam BigDecimal amount,
                                @RequestParam String description) {
        return accountService.transferFunds(fromAccountId, toAccountId, amount, description);
    }
}
