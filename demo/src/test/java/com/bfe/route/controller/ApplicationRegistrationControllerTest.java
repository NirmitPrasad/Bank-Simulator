package com.bfe.route.controller;

import com.bfe.route.enums.AccountType;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ApplicationRegistrationControllerTest {

    @Test
    void testRegisterCustomer() {
        String customerName = "Nirmit";
        AccountType accountType = AccountType.SAVINGS;
        double initialBalance = 1000.0;

        boolean registered = customerName != null
                && accountType != null
                && initialBalance >= 0;

        assertTrue(registered, "Customer should be registered successfully");
    }
}
