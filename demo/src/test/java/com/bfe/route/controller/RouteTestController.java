package com.bfe.route.controller;

import com.bfe.route.enums.TransactionType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.junit.jupiter.api.Assertions.*;

public class RouteTestController {

    @Test
    void testTransfer() {
        double acc1 = 2000;
        double acc2 = 1000;
        double amount = 500;

        acc1 -= amount;
        acc2 += amount;

        assertEquals(1500, acc1);
        assertEquals(1500, acc2);
    }

    // Example: same handler different transaction types
    @ParameterizedTest
    @EnumSource(value = TransactionType.class, names = {"DEPOSIT","WITHDRAW"})
    void testDepositWithdrawRouting(TransactionType type) {
        double balance = 1000;
        double amount  = 400;

        switch (type) {
            case DEPOSIT -> balance += amount;
            case WITHDRAW -> balance -= amount;
            default -> fail("Unexpected type");
        }

        if (type == TransactionType.DEPOSIT) {
            assertEquals(1400, balance);
        } else {
            assertEquals(600, balance);
        }
    }
}
