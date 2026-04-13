package com.base.app.auth.dto;

import com.base.app.employee.dto.EmployeeDto;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

public record LoginResponseDto(
        String accessToken,
        String tokenType,
        Long expiresIn,
        EmployeeDto employee,
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        LocalDateTime loginTime
) {

    public static LoginResponseDto of(String accessToken, String tokenType, Long expiresIn, EmployeeDto employee) {
        return new LoginResponseDto(accessToken, tokenType, expiresIn, employee, LocalDateTime.now());
    }

    public boolean isValid() {
        return accessToken != null && !accessToken.isEmpty() && employee != null;
    }

    public LocalDateTime expiresAt() {
        return loginTime.plusSeconds(expiresIn / 1000);
    }
}
