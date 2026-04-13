package com.base.domain.user.domain.valueobjects;

import jakarta.validation.constraints.NotBlank;

public record Password(@NotBlank String value) {

    public Password {
        if (value == null || value.length() < 8) {
            throw new IllegalArgumentException("Password must be at least 8 characters");
        }
        if (value.length() > 100) {
            throw new IllegalArgumentException("Password must not exceed 100 characters");
        }
    }

    public static Password of(String value) {
        return new Password(value);
    }

    public static Password raw(String rawPassword) {
        validateStrength(rawPassword);
        return new Password(rawPassword);
    }

    private static void validateStrength(String password) {
        if (password == null || password.length() < 8) {
            throw new IllegalArgumentException("Password must be at least 8 characters");
        }

        boolean hasUpper = password.chars().anyMatch(Character::isUpperCase);
        boolean hasLower = password.chars().anyMatch(Character::isLowerCase);
        boolean hasDigit = password.chars().anyMatch(Character::isDigit);
        boolean hasSpecial = password.chars().anyMatch(ch -> "!@#$%^&*()_+-=[]{}|;:,.<>?".indexOf(ch) >= 0);

        if (!(hasUpper && hasLower && hasDigit && hasSpecial)) {
            throw new IllegalArgumentException(
                    "Password must contain uppercase, lowercase, digit, and special character");
        }
    }

    public boolean isStrong() {
        try {
            validateStrength(value);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}