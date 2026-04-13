package com.base.app.auth.handler;

import com.base.app.auth.command.LoginEmailCommand;
import com.base.app.auth.dto.LoginResponseDto;
import com.base.app.employee.dto.EmployeeDto;
import com.base.domain.employee.domain.Employee;
import com.base.domain.employee.domain.EmployeeRole;
import com.base.domain.employee.repository.EmployeeRepository;
import com.base.domain.user.domain.valueobjects.Email;
import com.base.infra.config.security.service.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ManagerLoginHandler {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final EmployeeRepository employeeRepository;

    @Transactional(readOnly = true)
    public LoginResponseDto handle(LoginEmailCommand command) {
        log.info("Operations portal login attempt for email: {}", command.email());

        Employee employee = employeeRepository.findByEmail(Email.of(command.email()))
                .orElseThrow(() -> new BadCredentialsException("Invalid credentials"));

        if (employee.getRole() != EmployeeRole.MANAGER && employee.getRole() != EmployeeRole.STAFF) {
            log.warn("Security: employee {} with role {} attempted operations portal login", employee.getId(), employee.getRole());
            throw new BadCredentialsException("Access denied: only MANAGER or STAFF can use this portal.");
        }

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(command.email(), command.password())
        );

        String token = jwtService.generateToken(employee);
        Long expiry = jwtService.getExpirationTime();

        log.info("Operations portal login success for email: {}", command.email());
        return LoginResponseDto.of(token, "Bearer", expiry, EmployeeDto.fromDomain(employee));
    }
}
