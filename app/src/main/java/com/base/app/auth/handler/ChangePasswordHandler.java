package com.base.app.auth.handler;

import com.base.app.auth.command.ChangePasswordCommand;
import com.base.domain.employee.domain.Employee;
import com.base.domain.employee.repository.EmployeeRepository;
import com.base.domain.user.domain.valueobjects.Email;
import com.base.domain.user.domain.valueobjects.Password;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ChangePasswordHandler {

    private final EmployeeRepository employeeRepository;
    private final PasswordEncoder passwordEncoder;

    public void handle(ChangePasswordCommand command) {
        Employee employee = employeeRepository.findByEmail(Email.of(command.email()))
                .orElseThrow(() -> new IllegalArgumentException("Employee not found: " + command.email()));

        if (!passwordEncoder.matches(command.currentPassword(), employee.getPasswordHash())) {
            throw new IllegalArgumentException("Current password is incorrect");
        }

        Password.raw(command.newPassword());

        String encodedNewPassword = passwordEncoder.encode(command.newPassword());
        employee.changePassword(encodedNewPassword);

        employeeRepository.save(employee);
    }
}
