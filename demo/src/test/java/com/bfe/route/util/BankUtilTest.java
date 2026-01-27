package com.bfe.route.util;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Random;

public class BankUtilTest {

    @Test
    void testAccountNumberGeneration() {
        String accNum = generateAccountNumber();
        assertEquals(10, accNum.length());
    }

    private String generateAccountNumber() {
        Random rand = new Random();
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < 10; i++) {
            sb.append(rand.nextInt(10));
        }
        return sb.toString();
    }
}
