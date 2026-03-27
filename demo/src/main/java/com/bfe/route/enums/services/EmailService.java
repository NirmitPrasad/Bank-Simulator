package com.bfe.route.enums.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

@Service
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    @Value("${resend.api.key}")
    private String resendApiKey;

    @Value("${resend.from.email:onboarding@resend.dev}")
    private String fromEmail;

    private final RestTemplate restTemplate = new RestTemplate();
    private static final String RESEND_API_URL = "https://api.resend.com/emails";

    private void sendEmail(String toEmail, String subject, String htmlBody) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(resendApiKey);

        Map<String, Object> body = Map.of(
            "from", fromEmail,
            "to", List.of(toEmail),
            "subject", subject,
            "html", htmlBody
        );

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
        restTemplate.postForEntity(RESEND_API_URL, request, String.class);
    }

    public void sendOtpEmail(String toEmail,
                              String recipientName,
                              String otp,
                              int expiryMinutes) throws Exception {
        String subject = "Your One-Time Password (OTP)";
        String htmlBody = "<p>Dear " + (recipientName != null ? recipientName : "Customer") + ",</p>" +
                "<p>Your One-Time Password (OTP) for verification is:</p>" +
                "<p style=\"font-size:24px;font-weight:bold;letter-spacing:4px;color:#1a73e8\">" + otp + "</p>" +
                "<p>This OTP is valid for <b>" + expiryMinutes + " minutes</b>. Do not share this OTP with anyone.</p>" +
                "<br><p>Thank you for banking with us,<br><b>Bank Simulator Team</b></p>";
        try {
            sendEmail(toEmail, subject, htmlBody);
            logger.info("OTP email sent successfully to {}", toEmail);
        } catch (Exception e) {
            logger.error("Failed to send OTP email to {} : {}", toEmail, e.getMessage());
            throw e;
        }
    }

    public void sendTransactionEmail(String toEmail,
                                     String accountHolderName,
                                     String transactionType,
                                     String accountLastFourDigits,
                                     String amount,
                                     String utrRef,
                                     String balance,
                                     String dateTime) throws Exception {
        String subject = transactionType.equalsIgnoreCase("CREDIT")
                ? "Amount Credited to Your Account"
                : "Amount Debited from Your Account";

        String htmlBody = "<p>Dear " + accountHolderName + ",</p>" +
                "<p>An amount of <b>₹" + amount + "</b> has been " +
                (transactionType.equalsIgnoreCase("CREDIT") ? "credited " : "debited ") +
                "to your account ending with <b>" + accountLastFourDigits + "</b> on " + dateTime + ".</p>" +
                "<p>Transaction Reference: <b>" + utrRef + "</b><br>" +
                "Available Balance: <b>₹" + balance + "</b></p>" +
                "<br><p>Thank you for banking with us,<br><b>Bank Simulator Team</b></p>";
        try {
            sendEmail(toEmail, subject, htmlBody);
            logger.info("Transaction email sent successfully to {}", toEmail);
        } catch (Exception e) {
            logger.error("Failed to send transaction email to {} : {}", toEmail, e.getMessage());
            throw e;
        }
    }
}