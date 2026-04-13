package com.base.domain.user.domain.valueobjects;

import java.util.regex.Pattern;

public record Email(String value) {

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9+_.-]+@([A-Za-z0-9.-]+\\.[A-Za-z]{2,})$");

    public Email {
        if (value != null) {
            if (!EMAIL_PATTERN.matcher(value).matches()) {
                throw new IllegalArgumentException("Invalid email format: " + value);
            }
            // Normalize to lowercase
            value = value.toLowerCase();
        }
    }

    public static Email of(String value) {
        return new Email(value);
    }

    public String domain() {
        return value.substring(value.indexOf('@') + 1);
    }

    public String localPart() {
        return value.substring(0, value.indexOf('@'));
    }

    public boolean isGmail() {
        return "gmail.com".equals(domain());
    }
}