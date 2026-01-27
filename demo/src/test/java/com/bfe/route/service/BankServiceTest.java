package com.bfe.route.service;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class BankServiceTest {

    @Test
    void testInterestCalculation() {
        double principal = 10000;
        double rate = 5; // 5% annual
        int years = 2;

        double interest = (principal * rate * years) / 100;

        assertEquals(1000, interest);
    }

    @Test
    void testLoanEMICalculation() {
        double principal = 50000;
        double annualRate = 10; // annual interest rate
        int years = 2;

        double monthlyRate = annualRate / 12 / 100;
        int months = years * 12;

        double emi = (principal * monthlyRate * Math.pow(1 + monthlyRate, months)) /
                     (Math.pow(1 + monthlyRate, months) - 1);

        assertEquals(2307.25, Math.round(emi * 100.0) / 100.0);
    }
}
