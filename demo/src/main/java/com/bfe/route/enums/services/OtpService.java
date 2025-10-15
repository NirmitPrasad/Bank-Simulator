package com.bfe.route.enums.services;

import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class OtpService {

    private static class OtpEntry {
        final String otp;
        final Instant expiresAt;

        OtpEntry(String otp, Instant expiresAt) {
            this.otp = otp;
            this.expiresAt = expiresAt;
        }
    }

    private final Map<String, OtpEntry> emailToOtp = new ConcurrentHashMap<>();
    private final Random random = new Random();
    private final int expiryMinutes = 5;

    public String generateAndStoreOtp(String email) {
        String otp = String.format("%06d", random.nextInt(1_000_000));
        Instant expiresAt = Instant.now().plusSeconds(expiryMinutes * 60L);
        emailToOtp.put(email.toLowerCase(), new OtpEntry(otp, expiresAt));
        return otp;
    }

    public boolean verifyOtp(String email, String otp) {
        OtpEntry entry = emailToOtp.get(email.toLowerCase());
        if (entry == null) {
            return false;
        }
        if (Instant.now().isAfter(entry.expiresAt)) {
            emailToOtp.remove(email.toLowerCase());
            return false;
        }
        boolean ok = entry.otp.equals(otp);
        if (ok) {
            emailToOtp.remove(email.toLowerCase());
        }
        return ok;
    }

    public int getExpiryMinutes() {
        return expiryMinutes;
    }
}


