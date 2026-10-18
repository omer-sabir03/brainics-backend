package com.brainics.backend.common.validator;

import java.util.regex.Pattern;

public class PhoneValidator {
    private static final Pattern PAKISTAN_PHONE_PATTERN = Pattern.compile("^(\\+92|0)[0-9]{10}$");

    public static boolean isValidPakistanPhone(String phone) {
        if (phone == null || phone.isBlank()) {
            return false;
        }
        return PAKISTAN_PHONE_PATTERN.matcher(phone).matches();
    }

    public static String normalizePakistanPhone(String phone) {
        if (!isValidPakistanPhone(phone)) {
            throw new IllegalArgumentException("Invalid phone format: " + phone);
        }

        if (phone.startsWith("0")) {
            return "+92" + phone.substring(1);
        }

        return phone;
    }
}
