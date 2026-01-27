package com.bfe.route.enums.util;

import java.util.regex.Pattern;

public class ValidationUtil {

    private static final Pattern PHONE_PATTERN = Pattern.compile("^[6-9]\\d{9}$");
    private static final Pattern IFSC_PATTERN = Pattern.compile("^[A-Z]{4}0[A-Z0-9]{6}$");

    public static boolean isValidPhone(String phone) {
        return phone != null && PHONE_PATTERN.matcher(phone).matches();
    }

    public static boolean isValidIFSC(String ifsc) {
        return ifsc != null && IFSC_PATTERN.matcher(ifsc).matches();
    }

    public static boolean isValidName(String name) {
        return name != null && !name.trim().isEmpty();
    }

    public static boolean isValidAccountType(String type) {
        return type != null && (type.equalsIgnoreCase("savings") || type.equalsIgnoreCase("current"));
    }

    public static boolean isValidStatus(String status) {
        return status != null && (status.equalsIgnoreCase("active") || status.equalsIgnoreCase("inactive"));
    }

    public static boolean isValidBalance(Double balance) {
        return balance != null && balance >= 0;
    }

    public static boolean isValidSavingAmount(Double savingAmount) {
        return savingAmount != null && savingAmount >= 0;
    }
}
