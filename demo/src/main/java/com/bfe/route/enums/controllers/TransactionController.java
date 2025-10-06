package com.bfe.route.enums.controllers;

import com.bfe.route.enums.dto.TransactionRequestDto;
import com.bfe.route.enums.entity.Transaction;
import com.bfe.route.enums.services.AccountService;
import com.bfe.route.enums.services.TransactionService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.math.BigDecimal;

@RestController
@RequestMapping("/api/accounts")
public class TransactionController {

    @Autowired
    private AccountService accountService;

    @PostMapping("/{accountId}/deposit")
    public ResponseEntity<?> deposit(
            @PathVariable Long accountId,
            @Valid @RequestBody TransactionRequestDto request) {
        try {
            // Set type to DEPOSIT explicitly since this is deposit endpoint
            request.setType("DEPOSIT");
            
            if (request.getAmount() == null) {
                return ResponseEntity.badRequest().body("Amount is required");
            }

            Transaction transaction = accountService.processTransaction(
                accountId,
                request.getType(),
                request.getAmount(),
                request.getDescription()
            );

            return ResponseEntity.ok(transaction);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Deposit failed: " + e.getMessage());
        }
    }
}
