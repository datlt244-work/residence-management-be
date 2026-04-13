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
public class AdminLoginHandler {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final EmployeeRepository employeeRepository;

    @Transactional(readOnly = true)
    public LoginResponseDto handle(LoginEmailCommand command) {
        log.info("Admin portal login attempt for email: {}", command.email());

        Employee employee = employeeRepository.findByEmail(Email.of(command.email()))
                .orElseThrow(() -> new BadCredentialsException("Invalid admin credentials"));

        if (employee.getRole() != EmployeeRole.ADMIN) {
            log.warn("Security: employee {} with role {} attempted admin portal login", employee.getId(), employee.getRole());
            throw new BadCredentialsException("Access denied: only ADMIN can use this portal.");
        }

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(command.email(), command.password())
        );

        String accessToken = jwtService.generateToken(employee);
        Long expiresIn = jwtService.getExpirationTime();

        log.info("Admin login successful for email: {}", command.email());
        return LoginResponseDto.of(accessToken, "Bearer", expiresIn, EmployeeDto.fromDomain(employee));
    }
}
