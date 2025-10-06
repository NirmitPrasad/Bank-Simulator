package com.bfe.route.enums.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {
    
    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);
    
    @Value("${spring.mail.username}")
    private String fromEmail;
    
    @Autowired
    private JavaMailSender mailSender;
    
    public void sendTransactionEmail(String toEmail, 
                                   String accountHolderName,
                                   String transactionType,
                                   String accountLastFourDigits,
                                   String amount,
                                   String utrRef,
                                   String balance,
                                   String dateTime) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        
        helper.setFrom(fromEmail);
        helper.setTo(toEmail);
        helper.setSubject(transactionType.equalsIgnoreCase("CREDIT")
                ? "Amount Credited to Your Account"
                : "Amount Debited from Your Account");

        String body = "<p>Dear " + accountHolderName + ",</p>" +
                "<p>An amount of <b>₹" + amount + "</b> has been " +
                (transactionType.equalsIgnoreCase("CREDIT") ? "credited " : "debited ") +
                "to your account ending with <b>" + accountLastFourDigits + "</b> on " + dateTime + ".</p>" +
                "<p>Transaction Reference: <b>" + utrRef + "</b><br>" +
                "Available Balance: <b>₹" + balance + "</b></p>" +
                "<br><p>Thank you for banking with us,<br><b>Bank Simulator Team</b></p>";

        helper.setText(body, true);
        
        try {
            mailSender.send(message);
            logger.info("Transaction email sent successfully to {}", toEmail);
        } catch (Exception e) {
            logger.error("Failed to send transaction email to {} : {}", toEmail, e.getMessage());
            throw e;
        }
    }
}