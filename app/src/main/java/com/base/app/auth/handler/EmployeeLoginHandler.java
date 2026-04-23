package com.base.app.auth.handler;

import com.base.app.auth.command.LoginEmailCommand;
import com.base.app.auth.dto.LoginResponseDto;
import com.base.app.employee.dto.EmployeeDto;
import com.base.domain.employee.domain.Employee;
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
public class EmployeeLoginHandler {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final EmployeeRepository employeeRepository;

    @Transactional(readOnly = true)
    public LoginResponseDto handle(final LoginEmailCommand command) {
        log.info("Login attempt for email: {}", command.email());

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(command.email(), command.password()));

        final Employee employee =
                employeeRepository
                        .findByEmail(Email.of(command.email()))
                        .orElseThrow(() -> new BadCredentialsException("Invalid credentials"));

        final String accessToken = jwtService.generateToken(employee);
        final Long expiresIn = jwtService.getExpirationTime();

        log.info("Login successful for email: {} (role: {})", command.email(), employee.getRole());
        return LoginResponseDto.of(accessToken, "Bearer", expiresIn, EmployeeDto.fromDomain(employee));
    }
}
