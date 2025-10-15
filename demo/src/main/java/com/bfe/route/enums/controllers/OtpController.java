package com.bfe.route.enums.controllers;

import com.bfe.route.enums.services.EmailService;
import com.bfe.route.enums.services.OtpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/otp")
public class OtpController {

    @Autowired
    private OtpService otpService;

    @Autowired
    private EmailService emailService;

    @PostMapping("/send")
    public ResponseEntity<?> sendOtp(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String name = request.getOrDefault("name", "Customer");
        if (email == null || email.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Email is required"));
        }
        try {
            String otp = otpService.generateAndStoreOtp(email);
            emailService.sendOtpEmail(email, name, otp, otpService.getExpiryMinutes());
            return ResponseEntity.ok(Map.of("message", "OTP sent successfully"));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", "Failed to send OTP"));
        }
    }

    @PostMapping("/verify")
    public ResponseEntity<?> verifyOtp(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String otp = request.get("otp");
        if (email == null || otp == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Email and OTP are required"));
        }
        boolean ok = otpService.verifyOtp(email, otp);
        if (ok) {
            return ResponseEntity.ok(Map.of("valid", true));
        }
        return ResponseEntity.status(400).body(Map.of("valid", false, "error", "Invalid or expired OTP"));
    }
}


