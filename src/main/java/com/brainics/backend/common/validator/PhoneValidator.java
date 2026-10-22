package com.brainics.backend.common.validator;

import java.util.regex.Pattern;

public class PhoneValidator {
    private static final Pattern INDIAN_PHONE_PATTERN = Pattern.compile("^(\\+91|0)[6-9][0-9]{9}$");

    public static boolean isValidIndianPhone(String phone) {
        if (phone == null || phone.isBlank()) {
            return false;
        }
        return INDIAN_PHONE_PATTERN.matcher(phone).matches();
    }

    public static String normalizeIndianPhone(String phone) {
        if (!isValidIndianPhone(phone)) {
            throw new IllegalArgumentException("Invalid phone format: " + phone);
        }

        if (phone.startsWith("0")) {
            return "+91" + phone.substring(1);
        }

        return phone;
    }
}
