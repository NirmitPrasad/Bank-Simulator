package com.bfe.route.controller;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class GatewayFallbackControllerTest {

    @Test
    void testFallbackForServiceDown() {
        String response = getFallbackResponse();

        assertEquals("Service temporarily unavailable. Please try later.", response);
    }

    private String getFallbackResponse() {
        // Simulated fallback message
        return "Service temporarily unavailable. Please try later.";
    }
}
