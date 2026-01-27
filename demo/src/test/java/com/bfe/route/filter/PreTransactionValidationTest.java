package com.bfe.route.filter;

import com.bfe.route.enums.TransactionStatus;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PreTransactionValidationTest {

    @Test
    void testValidateSufficientBalance() {
        double balance = 1000;
        double withdrawAmount = 1200;

        boolean valid = balance >= withdrawAmount;
        TransactionStatus status = valid ? TransactionStatus.SUCCESS : TransactionStatus.FAILED;

        assertFalse(valid, "Transaction should fail due to insufficient balance");
        assertEquals(TransactionStatus.FAILED, status);
    }
}

